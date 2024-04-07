package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.example.App;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadingController implements Controller{
    @FXML
    public ScrollPane scrollPane;

    private PDDocument document;
    private PDFRenderer renderer;
    private VBox vbox = new VBox(5); // Kontener dla stron PDF jako obrazy

    @FXML
    private ListView<ImageView> listView;
    private List<Task<ImageView>> loadTasks = new ArrayList<>();
    private ObservableList<ImageView> pages = FXCollections.observableArrayList();

    public void initialize() {
        listView.setItems(pages);
        listView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ImageView> call(ListView<ImageView> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ImageView item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            setGraphic(item);
                        }
                    }
                };
            }
        });
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

                loadTasks.add(pageLoadTask);
                new Thread(pageLoadTask).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void cleanupBeforeLoadingNewFile() {
        // Anulowanie wszystkich trwających zadań ładowania stron
        for (Task<ImageView> task : loadTasks) {
            task.cancel();
        }
        loadTasks.clear(); // Wyczyść listę zadań

        // Opcjonalnie: zamknij i wyczyść obecny dokument
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

    @FXML
    private void close(ActionEvent e) {
        App.closeStage();
    }
    @FXML
    private void minimize(ActionEvent e) {
        App.minimizeStage();
    }
    @FXML
    private void setFullScreen(ActionEvent e) {
        App.setFullScreenStage();
    }
    @FXML
    private void switchToLibrary() throws IOException {
        cleanupBeforeLoadingNewFile(); // Anuluj trwające zadania i wyczyść listę przed ładowaniem nowego pliku
        vbox.getChildren().clear();
        App.setRoot("library");
    }


}
