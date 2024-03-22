package org.example.controller;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.example.App;

public class SecondaryController implements Controller, Initializable {
    @FXML
    public ListView<Node> listViewField;

    public ObservableList<Node> list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setListView();
    }

    public void setListView() {
        listViewField.setCellFactory(new Callback<ListView<Node>, ListCell<Node>>() {
            @Override
            public ListCell<Node> call(ListView<Node> listView) {
                return new ListCell<Node>() {
                    @Override
                    protected void updateItem(Node item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setPadding(Insets.EMPTY);
                            // Upewnij się, że item jest typu Region lub jego podklasy, aby można było wywołać prefWidthProperty
                            if (item instanceof Region) {
                                Region region = (Region) item;
                                region.prefWidthProperty().bind(listView.widthProperty().subtract(2)); // Możesz zmienić wartość odejmowaną, aby zarezerwować miejsce na scrollbar
                                setGraphic(item);
                            }
                        }
                    }
                };
            }
        });
    }

    public void addFileBar(File selectedFile) throws IOException {
        // Załaduj FXML dla fileBar i jednocześnie utwórz instancję kontrolera
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fileBar.fxml"));
        Node fileBar = loader.load(); // Ładuje Node, który jest elementem GUI
        FileBarController controller = loader.getController(); // Uzyskaj dostęp do kontrolera dla tego właśnie załadowanego elementu
        ReadingController rc = (ReadingController) SceneManager.getInstance().getController("reading");

        // Uaktualnij kontroler za pomocą nowej nazwy pliku
        controller.updateData(selectedFile.getName(), selectedFile.length());
        list.add(fileBar);
        listViewField.setItems(list);
        fileBar.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                try {
                    switchToReading();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                rc.loadPDFFile(selectedFile.getAbsolutePath());
            }
        });
    }

    private void switchToReading() throws IOException {
        App.setRoot("reading");
    }
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
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
}