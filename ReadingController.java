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

import java.io.IOException;

public class ReadingController implements Controller {

    @FXML
    private Button playButton;

    @FXML
    private ListView<ImageView> listView;
    private final ObservableList<ImageView> pages = FXCollections.observableArrayList();
    private final ObservableList<String> pagesOfText = FXCollections.observableArrayList();
    private final PDFDocumentHandler pdfDocumentHandler = new PDFDocumentHandler(pages, pagesOfText);
    private Process speechProcess = null;

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
    private void readText(String text) {
        // Zatrzymaj aktualnie mówiący proces, jeśli istnieje
        if (speechProcess != null) {
            speechProcess.destroy();
            speechProcess = null;
            return; // Przerywamy dalsze wykonywanie metody, jeśli zatrzymujemy odczyt
        }

        new Thread(() -> {
            try {
                String command = "\"C:\\Program Files\\eSpeak NG\\espeak-ng.exe\" -v pl \"" + text + "\"";
                speechProcess = Runtime.getRuntime().exec(command);
                speechProcess.waitFor(); // Czekaj na zakończenie procesu
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                speechProcess = null; // Wyczyść referencję po zakończeniu procesu
            }
        }).start();
    }

    @FXML
    private void playButtonAction(ActionEvent e) {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < pagesOfText.size()) {
            String textToRead = pagesOfText.get(selectedIndex + 1);
            readText(textToRead);
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
