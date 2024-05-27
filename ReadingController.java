package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.example.App;
import org.example.model.PDFDocumentHandler;
import org.example.model.SpeechManager;

import java.io.IOException;

public class ReadingController implements Controller {
    private final ObservableList<ImageView> pages = FXCollections.observableArrayList();
    private final ObservableList<String> pagesOfText = FXCollections.observableArrayList();
    private final PDFDocumentHandler pdfDocumentHandler = new PDFDocumentHandler(pages, pagesOfText);
    private final SpeechManager sm = SpeechManager.getInstance();
    @FXML
    public Button playButton;
    @FXML
    private ListView<ImageView> listView;

    public void initialize() {
        listView.setItems(pages);
        defineCellFactory();
        choosePageListener();
    }

    private void defineCellFactory() {
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
    private void choosePageListener() {
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // indeks strony
                int currentPageIndex = listView.getSelectionModel().getSelectedIndex();
                System.out.println("Aktualnie wybrana strona: " + (currentPageIndex + 1));
            }
        });
    }
    public PDFDocumentHandler getPdfDocumentHandler() {
        return pdfDocumentHandler;
    }
    @FXML
    private void playButtonAction(ActionEvent e) {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < pagesOfText.size()) {
            String textToRead = pagesOfText.get(selectedIndex + 1);
//            sm.readText(textToRead);
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
        pdfDocumentHandler.cleanupBeforeLoadingNewFile(); // Anuluj trwające zadania i wyczyść listę przed ładowaniem nowego pliku
        App.setRoot("library");
    }
}
