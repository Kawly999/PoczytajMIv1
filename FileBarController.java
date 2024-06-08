package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.File;
import java.util.Date;

public class FileBarController extends BarController implements Controller {
    @FXML
    public Text filename;
    @FXML
    public Text fileSize;
    @FXML
    public AnchorPane fileBar;
    @FXML
    public MenuItem deleteItem;
    public File file;
    public Date date;

    public void initialize() {
        date = new Date();
    }

    public void setFile(File file) {
        this.file = file;
    }
    public File getFile() {
        return file;
    }

    public MenuItem getDeleteItem() {
        return deleteItem;
    }

    public Node getStructure() {
        return fileBar;
    }
    public String getName() {
        return filename.getText();
    }

    @Override
    public Date getDate() {
        return date;
    }

    public void updateData(String filename, long fileSize) {
        this.filename.setText(filename);
        this.fileSize.setText(String.format("%.2f KB", fileSize/1024.0));
    }
}
