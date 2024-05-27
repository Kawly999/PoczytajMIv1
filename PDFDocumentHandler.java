package org.example.model;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PDFDocumentHandler {

    private PDDocument document;
    private PDFRenderer renderer;
    private final List<Task<ImageView>> loadTasksImage = new ArrayList<>();
    private final List<Task<String>> loadTasksText = new ArrayList<>();
    private final ObservableList<ImageView> pages;
    private final ObservableList<String> pagesOfText;
    private final DatabaseConnector dbConnector;

    public PDFDocumentHandler(ObservableList<ImageView> pages, ObservableList<String> pagesOfText) {
        this.pages = pages;
        this.pagesOfText = pagesOfText;
        this.dbConnector = new DatabaseConnector();
    }

    public void saveFileDataToDB(File selectedFile) throws IOException {
        PreparedStatement preparedStatement = null;
        String email = SharedVariables.getInstance().getEmail();
        System.out.println(email);
        ResultSet rs = null;
        int user_id = -1;
        String file_name = selectedFile.getName();
        String file_path = selectedFile.getPath();
        long file_size = selectedFile.length();
        Instant upload_date = Instant.now();
        Timestamp timestamp = Timestamp.from(upload_date);

        try (Connection connection = dbConnector.connect()) {
            preparedStatement = connection.prepareStatement("SELECT user_id FROM users WHERE email = ?");
            preparedStatement.setString(1, email);
            rs = preparedStatement.executeQuery();
            try {
                if (rs.next()) {
                    user_id = rs.getInt("user_id");
                    System.out.println(user_id);
                }
            } catch (SQLException e) {e.printStackTrace();}
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO files (user_id, file_name, file_path, file_size, upload_date, mime_type ) VALUES (?, ?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, user_id);
            preparedStatement.setString(2, file_name);
            preparedStatement.setString(3, file_path);
            preparedStatement.setLong(4, file_size);
            preparedStatement.setTimestamp(5, timestamp);
            preparedStatement.setString(6, getMIME(file_path));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String getMIME(String path) {
        String extension = extractExtension(path);
        StringBuilder sb = new StringBuilder();
        sb.append("application/");
        sb.append(extension);
        return sb.toString();
    }
    public String extractExtension(String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }

    public void extractTextFromPDF(String pdfFile) {
        try {
            document = PDDocument.load(new File(pdfFile));
            renderer = new PDFRenderer(document);
            int numberOfPages = document.getNumberOfPages();

            for (int i = 0; i < numberOfPages; i++) {
                pagesOfText.add(null); // Dodajemy placeholder
            }

            for (int i = 0; i < numberOfPages; i++) {
                final int pageIndex = i;
                Task<String> loadTextFromPage = new Task<>() {
                    @Override
                    protected String call() throws Exception {
                        if (isCancelled()) {
                            return null;
                        }
                        PDFTextStripper stripper = new PDFTextStripper();
                        stripper.setStartPage(pageIndex);
                        stripper.setEndPage(pageIndex);
                        return stripper.getText(document);
                    }
                };
                loadTextFromPage.setOnSucceeded(e -> {
                    if (!loadTextFromPage.isCancelled()) {
                        pagesOfText.set(pageIndex, loadTextFromPage.getValue());
                    }
                });
                loadTasksText.add(loadTextFromPage);
                new Thread(loadTextFromPage).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadPDFFile(String pdfFile) {
        try {
            document = PDDocument.load(new File(pdfFile));
            renderer = new PDFRenderer(document);
            int numberOfPages = document.getNumberOfPages();

            for (int i = 0; i < numberOfPages; i++) {
                pages.add(null); // Dodajemy placeholder
            }

            for (int i = 0; i < numberOfPages; i++) {
                final int pageIndex = i;
                Task<ImageView> pageLoadTask = new Task<>() {
                    @Override
                    protected ImageView call() throws Exception {
                        // Sprawdzenie, czy zadanie nie zostało anulowane przed przystąpieniem do ładowania
                        if (isCancelled()) {
                            return null;
                        }
                        BufferedImage bufferedImage = renderer.renderImageWithDPI(pageIndex, 96, ImageType.RGB);
                        ImageView imageView = new ImageView(SwingFXUtils.toFXImage(bufferedImage, null));
                        imageView.setPreserveRatio(true);
                        return imageView;
                    }
                };

                pageLoadTask.setOnSucceeded(e -> {
                    if (!pageLoadTask.isCancelled()) {
                        pages.set(pageIndex, pageLoadTask.getValue());
                    }
                });

                loadTasksImage.add(pageLoadTask);
                new Thread(pageLoadTask).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void cleanupBeforeLoadingNewFile() {
        // Anulowanie wszystkich trwających zadań ładowania stron
        for (Task<ImageView> task : loadTasksImage) {
            task.cancel();
        }
        loadTasksImage.clear(); // Wyczyść listę zadań

        // zamknij i wyczyść obecny dokument
        if (document != null) {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            document = null;
        }

        // Wyczyść listę stron
        pages.clear();
    }
}
