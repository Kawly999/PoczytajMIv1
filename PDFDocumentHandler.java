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
import java.util.ArrayList;
import java.util.List;

public class PDFDocumentHandler {

    private PDDocument document;
    private PDFRenderer renderer;
    private List<Task<ImageView>> loadTasksImage = new ArrayList<>();
    private List<Task<String>> loadTasksText = new ArrayList<>();
    private ObservableList<ImageView> pages;
    private ObservableList<String> pagesOfText;

    public PDFDocumentHandler(ObservableList<ImageView> pages, ObservableList<String> pagesOfText) {
        this.pages = pages;
        this.pagesOfText = pagesOfText;
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
}
