package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class FileBarController implements Controller{
    @FXML
    public Text filename;

    @FXML
    public Text fileSize;

    public void updateData(String filename, long fileSize) {
        this.filename.setText(filename);
        this.fileSize.setText(String.format("%.2f KB", fileSize/1024.0));
    }
}
