package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.File;
import java.util.Date;


public class FolderBarController extends BarController implements Controller {
    @FXML
    private Text folderName;
    @FXML
    public Button folderListButton;
    @FXML
    public ContextMenu contextMenu;
    @FXML
    private StackPane stackPane;
    public int fileCounter = 0;
    public Date crateDate;

    private final ObservableMap<Integer, File> files = FXCollections.observableHashMap();
    private TextField textFieldFolderName;
    @FXML
    public AnchorPane folderBar;

    @FXML
    public void initialize() {
        crateDate = new Date();
        textFieldFolderName = new TextField();
        stackPane.getChildren().add(textFieldFolderName);
        textFieldFolderName.setVisible(false);

        textFieldFolderName.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                updateText();
            }
        });

        folderName.setOnMouseClicked(e -> activateTextField());

        openFilesListInFolder();
    }

    public String getName() {
        return folderName.getText();
    }

    @Override
    public Date getDate() {
        return null;
    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }
    public ObservableMap<Integer, File> getFiles() {
        return files;
    }

    public void setNewMenuItem(FileBarController newMenuItem) {
        files.put(fileCounter, newMenuItem.getFile());
        MenuItem menuItem = new MenuItem(newMenuItem.getName());
        menuItem.setId(String.valueOf(fileCounter));
        fileCounter++;
        contextMenu.getItems().add(menuItem);
    }

    @Override
    public Node getStructure() {
        return folderBar;
    }

    private void openFilesListInFolder() {
        folderListButton.setOnMouseClicked( e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                contextMenu.show(folderListButton, e.getScreenX(), e.getScreenY());
            }
        });
    }

    private void activateTextField() {
        textFieldFolderName.setText(folderName.getText());
        textFieldFolderName.setVisible(true);
        folderName.setVisible(false);
        textFieldFolderName.requestFocus(); // ustawia fokus, czyli input klawiatury na textField
    }

    private void updateText() {
        if (!textFieldFolderName.getText().isEmpty()) {
            folderName.setText(textFieldFolderName.getText());
        }
        textFieldFolderName.setVisible(false);
        folderName.setVisible(true);
    }
}
