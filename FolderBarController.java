package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;



public class FolderBarController implements Controller {
    @FXML
    private Text folderName;
    @FXML
    private StackPane stackPane;
    private final ObservableList<FileBarController> files = FXCollections.observableArrayList();
    private TextField textFieldFolderName;

    @FXML
    public void initialize() {
        textFieldFolderName = new TextField();
        stackPane.getChildren().add(textFieldFolderName);
        textFieldFolderName.setVisible(false);

        textFieldFolderName.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                updateText();
            }
        });

        folderName.setOnMouseClicked(e -> {
            activateTextField();
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
