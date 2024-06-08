package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.example.App;
import org.example.model.PDFDocumentHandler;
import org.example.model.SpeechManager;
import org.example.view.ChangePrimarySceneTheme;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReadingController implements Controller {

    // Hbox
    @FXML
    public HBox h1;
    @FXML
    public HBox h2;
    @FXML
    public HBox h3;
    @FXML
    public HBox h4;
    @FXML
    public HBox h5;
    @FXML
    public HBox h6;
    // Przyciski
    @FXML
    public ToggleButton toggleButton;
    @FXML
    public Button addPdfButton;
    @FXML
    public MenuButton MBJ;
    @FXML
    public Button moveLeftButton;
    @FXML
    public Button moveRightButton;
    @FXML
    public Button middleButton;
    // Pozostałe
    @FXML
    public TextField clock;
    @FXML
    public AnchorPane anchorPane;
    @FXML
    public Circle circle;
    @FXML
    public TextField speed;
    @FXML
    private ListView<ImageView> listView;
    public Map<String, HBox> hBoxMap = new HashMap<>();
    private String readingSpeed;
    private String language;
    private String gender;
    private String dialect;
    private final ObservableList<ImageView> pages = FXCollections.observableArrayList();
    private final ObservableList<String> pagesOfText = FXCollections.observableArrayList();
    private final PDFDocumentHandler pdfDocumentHandler = new PDFDocumentHandler(pages, pagesOfText);
    private final SpeechManager sm = SpeechManager.getInstance();
    public static boolean stopRequest = false;
    public static boolean started = false;
    public static int leftClicks = 0;
    public static int rightClicks = 0;


    public void initialize() {
        hBoxMap = Map.of("h1", h1, "h2", h2, "h3", h3, "h4", h4, "h5", h5, "h6", h6);

        anchorPane.getStylesheets().add(getClass().getResource("/button.css").toExternalForm());
        toggleButton.getStyleClass().add("button-light");
        listView.setItems(pages);
        defineCellFactory();
        setSpeed();
        setLanguage();
        middleButton.setOnMouseClicked(event -> {
            int clickCount = event.getClickCount();
            if (clickCount == 2) {
                play();
                started = true;
            } else if (clickCount == 1 && started) {
                stopRequest = true;
            }
        });
        moveLeftButton.setOnMouseClicked(event -> {
            leftClicks = event.getClickCount();
        });
        moveRightButton.setOnMouseClicked(event -> {
            rightClicks = event.getClickCount();
        });
    }

    private void defineCellFactory() {
        listView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ImageView> call(ListView<ImageView> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ImageView item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            setGraphic(item);
                        }
                    }
                };
            }
        });
    }
    public PDFDocumentHandler getPdfDocumentHandler() {
        return pdfDocumentHandler;
    }

    public void play() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        System.out.println("play f" + selectedIndex);
        String text = pagesOfText.get(selectedIndex);
        String timer = clock.getText();
        sm.readText(text, readingSpeed, language, dialect, gender, timer, middleButton, moveLeftButton, moveRightButton, false);
    }
    public void setLanguage() {
        final String[] id = new String[1];
        language = "pl";
        dialect = "";
        gender = "m1";
        for (int i = 0; i < MBJ.getItems().size(); i++) {
            MenuItem item = MBJ.getItems().get(i);
            item.setOnAction((e) -> {
                id[0] = item.getId();
                switch (id[0]) {
                    case "PLM": {
                        language = "pl";
                        dialect = "";
                        gender = "m1";
                        break;
                    }
                    case "PLK": {
                        language = "pl";
                        dialect = "";
                        gender = "f1";
                        break;
                    }
                    case "ANGM": {
                        language = "en";
                        dialect = "us";
                        gender = "m1";
                        break;
                    }
                    case "ANGK": {
                        language = "en";
                        dialect = "us";
                        gender = "f1";
                        break;
                    }
                }
            });
        }
    }

    public void setSpeed() {
        readingSpeed = "175";
        speed.setText(readingSpeed);
        speed.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                String readingSpeed = speed.getText();
                speed.setText(readingSpeed);
                this.readingSpeed = readingSpeed;
            }
        });
    }
    public void addPdf(ActionEvent e) throws IOException {
        SecondaryController sc = (SecondaryController)SceneManager.getInstance().getController("library");

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null) {
            sc.addFileBar(selectedFile);
            switchToLibrary();
            sc.listView.refresh();
        } else {
            System.out.println("File is not valid");
        }
    }
    @FXML
    private void toggleSceneColor() {
        ChangePrimarySceneTheme.setThemeOnTButton(hBoxMap, toggleButton, anchorPane, circle);
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
        pdfDocumentHandler.cleanupBeforeLoadingNewFile(); // Anuluj trwające zadania i wyczyść listę przed ładowaniem nowego pliku
        App.setRoot("library");
    }
}
