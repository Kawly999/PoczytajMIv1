package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.example.App;

import java.io.File;
import java.io.IOException;

public class PrimaryController implements Controller {
    @FXML
    public TextArea textArea; // pole tekstowe zdefiniowane w FXML
    public Button closeButton;
    @FXML
    public Button addPdfButton;
    private Parent root;
    public void addPdf(ActionEvent e) throws IOException {
        SecondaryController sc = (SecondaryController)SceneManager.getInstance().getController("library");

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null) {
            sc.addFileBar(selectedFile);
            switchToLibrary();
            sc.listViewField.refresh();
        } else {
            System.out.println("File is not valid");
        }
    }

    @FXML
    public void play(ActionEvent e) {
        String text = textArea.getText();
        readText(text);
    }
    private void readText(String text) {
        try {
            String command = "\"C:\\Program Files\\eSpeak NG\\espeak-ng.exe\" -v pl \"" + text + "\"";
            Runtime.getRuntime().exec(command);
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
