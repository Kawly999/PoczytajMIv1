package org.example.controller;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import java.io.File;
import java.sql.SQLException;
import java.util.Comparator;

import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import org.example.App;
import org.example.model.CloudStorageManager;

public class SecondaryController implements Controller {
    @FXML
    ListView<BarController> listView;
    private final ObservableList<BarController> list;
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
            public ListCell<BarController> call(ListView<BarController> listView) {
                ListCell<BarController> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(BarController item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            setPadding(Insets.EMPTY);
                            setGraphic(item.getStructure());
                        }
                    }
                };

                // Ustawienie obsługi przeciągania elementów
                cell.setOnDragDetected(event -> {
                    if (cell.getItem() == null) {
                        return;
                    }
                    Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(Integer.toString(cell.getIndex()));
                    dragboard.setContent(content);

                    event.consume();
                });

                cell.setOnDragOver(event -> {
                    if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });

                cell.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        int draggedIndex = Integer.parseInt(db.getString());
                        int thisIndex = cell.getIndex();

                        // ustawienie elementu na koniec, jeśli zamieniam go z pustą komórką
                        if (thisIndex < 0 || thisIndex >= listView.getItems().size()) {
                            BarController draggedNode = listView.getItems().get(draggedIndex);
                            thisIndex = listView.getItems().size() - 1;
                            listView.getItems().remove(draggedIndex);
                            listView.getItems().add(thisIndex, draggedNode);
                        }
                        else {
                            // wstawienie elementu do folderu
                            if (listView.getItems().get(thisIndex) instanceof FolderBarController) {
                                FolderBarController folderBC = (FolderBarController) listView.getItems().get(thisIndex);
                                FileBarController fileBC = (FileBarController) listView.getItems().get(draggedIndex);
                                folderBC.setNewMenuItem(fileBC);
                                listView.getItems().remove(draggedIndex);

                            }
                            // zamienienie dwóch elementów
                            else {
                                FileBarController draggedNode = (FileBarController) listView.getItems().get(draggedIndex);
                                listView.getItems().remove(draggedIndex);
                                listView.getItems().add(thisIndex, draggedNode);
                            }
                        }

                        listView.refresh();

                        success = true;
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });

                return cell;
            }
        });
    }

    @FXML
    public void addFolder() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/folderBar.fxml"));
        Node folderBar = loader.load(); // Ładuje customowy node
        FolderBarController controller = loader.getController();
        ReadingController rc = (ReadingController) SceneManager.getInstance().getController("reading");

        list.add(controller);
        listView.setItems(list);
        openFile(controller.getFiles(), controller.getContextMenu(), rc);
    }

    // metoda do wczytywania wszystkich plików pobranych z google cloud storage
    public void addFileBarReadingGCS(File selectedFile) throws IOException {
        // Załaduj FXML dla fileBar i jednocześnie utwórz instancję kontrolera
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fileBar.fxml"));
        Node fileBar = loader.load(); // Ładuje customowy node
        FileBarController controller = loader.getController();
        ReadingController rc = (ReadingController) SceneManager.getInstance().getController("reading");

        // Uaktualnij kontroler za pomocą nowej nazwy pliku
        controller.setFile(selectedFile);
        controller.updateData(selectedFile.getName(), selectedFile.length());

        list.add(controller);
        listView.setItems(list);
        openFileAndSaveToCloudIfNeeded(selectedFile, fileBar, rc, false);
    }


    public void addFileBar(File selectedFile) throws IOException {
        // Załaduj FXML dla fileBar i jednocześnie utwórz instancję kontrolera
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fileBar.fxml"));
        Node fileBar = loader.load(); // Ładuje customowy node
        FileBarController controller = loader.getController(); // Uzyskaj dostęp do kontrolera dla tego właśnie załadowanego elementu
        ReadingController rc = (ReadingController) SceneManager.getInstance().getController("reading");

        // Uaktualnij kontroler za pomocą nowej nazwy pliku
        controller.setFile(selectedFile);
        controller.updateData(selectedFile.getName(), selectedFile.length());

        list.add(controller);
        listView.setItems(list);
        openFileAndSaveToCloudIfNeeded(selectedFile, fileBar, rc, true);
    }

    private void openFile(ObservableMap<Integer, File> files, ContextMenu contextMenu, ReadingController rc) {
        contextMenu.getItems().addListener((ListChangeListener.Change<? extends MenuItem> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (MenuItem addedItem : c.getAddedSubList()) {
                        addedItem.setOnAction(event -> {
                            try {
                                switchToReading();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            int selectedItemID = Integer.parseInt(addedItem.getId());
                            File chosenFile = files.get(selectedItemID);
                            rc.getPdfDocumentHandler().extractTextFromPDF(chosenFile.getAbsolutePath());
                            rc.getPdfDocumentHandler().loadPDFFile(chosenFile.getAbsolutePath());
                        });
                    }
                }
            }
        });
    }

    private void openFileAndSaveToCloudIfNeeded(File selectedFile, Node fileBar, ReadingController rc, boolean saveToFile) {
        fileBar.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                try {
                    switchToReading();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (saveToFile) {
                    try {
                        rc.getPdfDocumentHandler().saveFileDataToDB(selectedFile);
                        csm.sendFileToCloudStorage(selectedFile);
                    } catch (IOException | SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                rc.getPdfDocumentHandler().extractTextFromPDF(selectedFile.getAbsolutePath());
                rc.getPdfDocumentHandler().loadPDFFile(selectedFile.getAbsolutePath());
            }
        });
    }
    @FXML
    public void sortListViewAlphabetically() {listView.getItems().sort(Comparator.comparing(BarController::toString));}
    @FXML
    public void sortListViewByDate() {listView.getItems().sort(Comparator.comparing(BarController::getDate, Comparator.nullsLast(Comparator.naturalOrder())));}

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