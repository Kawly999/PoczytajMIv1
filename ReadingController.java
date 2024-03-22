package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.example.App;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ReadingController implements Controller{
    @FXML
    public ImageView pdfImageView;

    public void loadPDFFile(String pdfFile) {
        try (PDDocument document = PDDocument.load(new File(pdfFile))) {
            PDFRenderer renderer = new PDFRenderer(document);
            // Renderowanie pierwszej strony dokumentu PDF
            BufferedImage bufferedImage = renderer.renderImageWithDPI(0, 300, ImageType.RGB); // Renderowanie z DPI 300 dla lepszej jakości
            Image image = javafx.embed.swing.SwingFXUtils.toFXImage(bufferedImage, null); // Konwersja na obiekt Image JavaFX

            pdfImageView.setImage(image);

            pdfImageView.setFitWidth(800); // Możesz dostosować szerokość do swoich potrzeb
            pdfImageView.setPreserveRatio(true);
            pdfImageView.setSmooth(true);
            pdfImageView.setCache(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        App.setRoot("library");
    }


}
