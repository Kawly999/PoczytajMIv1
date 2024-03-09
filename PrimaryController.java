package org.example.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.example.App;

public class PrimaryController {
    @FXML
    public TextArea textArea; // pole tekstowe zdefiniowane w FXML
    public Button closeButton;

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
