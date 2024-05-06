package org.example.controller;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import java.io.File;
import java.sql.SQLException;

import javafx.scene.control.MenuBar;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.example.App;
import org.example.model.CloudStorageManager;

public class SecondaryController implements Controller {
    @FXML
    ListView<Node> listView;
    private final ObservableList<Node> list;
    private final CloudStorageManager csm;

    public SecondaryController() throws IOException {
        list = FXCollections.observableArrayList();
        csm = CloudStorageManager.getInstance();
    }

    public void initialize() {
        setListView();
    }

    public void setListView() {
        listView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Node> call(ListView<Node> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Node item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setPadding(Insets.EMPTY);
                            if (item instanceof Region) {
                                Region region = (Region) item;
                                region.prefWidthProperty().bind(listView.widthProperty().subtract(2));
                                setGraphic(item);
                            }
                        }
                    }
                };
            }
        });
    }


    @FXML
    public void addFolder() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/folderBar.fxml"));
        Node folderBar = loader.load(); // Ładuje customowy node
        FolderBarController controller = loader.getController();
        ReadingController rc = (ReadingController) SceneManager.getInstance().getController("reading");

        list.add(folderBar);
        listView.setItems(list);
    }

    // metoda do wczytywania wszystkich plików pobranych z google cloud storage
    public void addFileBarReadingGCS(File selectedFile) throws IOException {
        // Załaduj FXML dla fileBar i jednocześnie utwórz instancję kontrolera
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fileBar.fxml"));
        Node fileBar = loader.load(); // Ładuje customowy node
        FileBarController controller = loader.getController();
        ReadingController rc = (ReadingController) SceneManager.getInstance().getController("reading");

        // Uaktualnij kontroler za pomocą nowej nazwy pliku
        controller.updateData(selectedFile.getName(), selectedFile.length());
        list.add(fileBar);
        listView.setItems(list);
        fileBar.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                try {
                    switchToReading();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                rc.getPdfDocumentHandler().extractTextFromPDF(selectedFile.getAbsolutePath());
                rc.getPdfDocumentHandler().loadPDFFile(selectedFile.getAbsolutePath());
            }
        });
    }


    public void addFileBar(File selectedFile) throws IOException {
        // Załaduj FXML dla fileBar i jednocześnie utwórz instancję kontrolera
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fileBar.fxml"));
        Node fileBar = loader.load(); // Ładuje customowy node
        FileBarController controller = loader.getController(); // Uzyskaj dostęp do kontrolera dla tego właśnie załadowanego elementu
        ReadingController rc = (ReadingController) SceneManager.getInstance().getController("reading");

        // Uaktualnij kontroler za pomocą nowej nazwy pliku
        controller.updateData(selectedFile.getName(), selectedFile.length());
        list.add(fileBar);
        listView.setItems(list);
        fileBar.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                try {
                    switchToReading();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    rc.getPdfDocumentHandler().saveFileDataToDB(selectedFile);
                    csm.sendFileToCloudStorage(selectedFile);
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
                rc.getPdfDocumentHandler().extractTextFromPDF(selectedFile.getAbsolutePath());
                rc.getPdfDocumentHandler().loadPDFFile(selectedFile.getAbsolutePath());
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