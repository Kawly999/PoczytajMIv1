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
    public int filesCounter = 0;
    public Date date;
    private final ObservableMap<Integer, File> files = FXCollections.observableHashMap();
    @FXML
    private StackPane stackPane;
    @FXML
    private Text folderName;
    @FXML
    public Button folderListButton;
    @FXML
    public ContextMenu contextMenu;
    @FXML
    private TextField textFieldFolderName;
    @FXML
    public AnchorPane folderBar;
    @FXML
    public MenuItem deleteItem;

    @FXML
    public void initialize() {
        date = new Date();
        textFieldFolderName = new TextField();
        stackPane.getChildren().add(textFieldFolderName);
        textFieldFolderName.setVisible(false);
        folderName.setOnMouseClicked(e -> activateTextField());
        acceptNewFolderName();
        openFilesListInFolder();
    }

    private void acceptNewFolderName() {
        textFieldFolderName.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                updateText();
            }
        });
    }

    public void setNewMenuItem(FileBarController newMenuItem) {
        files.put(filesCounter, newMenuItem.getFile());
        MenuItem menuItem = new MenuItem(newMenuItem.getName());
        menuItem.setId(String.valueOf(filesCounter));
        filesCounter++;
        contextMenu.getItems().add(menuItem);
    }
    // ustawienie, aby listę plików w folderze można było otworzyć prawym klawiszem myszy (domyślnie lewym) 
    private void openFilesListInFolder() {
        folderListButton.setOnMouseClicked( e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                contextMenu.show(folderListButton, e.getScreenX(), e.getScreenY());
            }
        });
    }
    // zamiana widoczności nodów z tekstu na pole z możliwościa wpisywania
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

    public MenuItem getDeleteItem() {
        return deleteItem;
    }

    public String getName() {
        return folderName.getText();
    }
    @Override
    public Date getDate() {
        return date;
    }
    // pobiera główny Node/Pane w celu ustawienia wyglądu 
    @Override
    public Node getStructure() {
        return folderBar;
    }
    public ContextMenu getContextMenu() {
        return contextMenu;
    }
    public ObservableMap<Integer, File> getFiles() {
        return files;
    }
}
