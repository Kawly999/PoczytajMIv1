package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.example.App;
import org.example.model.SpeechManager;
import org.example.view.ChangePrimarySceneTheme;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PrimaryController implements Controller {
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
    public Button arrowUp;
    @FXML
    public Button arrowDown;
    @FXML
    public MenuButton MBJ;
    @FXML
    public Button moveLeftButton;
    @FXML
    public Button moveRightButton;
    @FXML
    public Button middleButton;
    // Fonty
    @FXML
    public MenuItem Arial;
    @FXML
    public MenuItem Times;
    @FXML
    public MenuItem Verdana;
    @FXML
    public MenuItem Helvetica;
    @FXML
    public MenuItem Calibri;
    // Pozostałe
    @FXML
    public TextField clock;
    @FXML
    public TextArea textArea;
    @FXML
    public AnchorPane anchorPane;
    @FXML
    public Circle circle;
    @FXML
    public TextField speed;
    public Map<String, HBox> hBoxMap = new HashMap<>();
    private final SpeechManager sm = SpeechManager.getInstance();
    private String readingSpeed;
    private String language;
    private String gender;
    private String dialect;
    public static boolean stopRequest = false;
    public static boolean started = false;
    public static int leftClicks = 0;
    public static int rightClicks = 0;


    public void initialize() {
        // konstruktor nie ma dostępu do pól @FXML
        hBoxMap = Map.of("h1", h1, "h2", h2, "h3", h3, "h4", h4, "h5", h5, "h6", h6);
        textArea.setWrapText(true);

        anchorPane.getStylesheets().add(getClass().getResource("/button.css").toExternalForm());
        toggleButton.getStyleClass().add("button-light");
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

    public void play() {
        String text = textArea.getText();
        String timer = clock.getText();
        sm.readText(text, readingSpeed, language, dialect, gender, timer, middleButton, moveLeftButton, moveRightButton, true);
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

    @FXML
    private void increaseFontSize(ActionEvent e) {
        if (textArea.getFont().getSize() <= 72) {
            textArea.setFont(Font.font(textArea.getFont().getSize() + 2));
        }
    }
    @FXML
    private void decreaseFontSize(ActionEvent e) {
        if (textArea.getFont().getSize() >= 8) {
            textArea.setFont(Font.font(textArea.getFont().getSize() - 2));
        }
    }

    @FXML
    private void setArial(ActionEvent e) {textArea.setFont(Font.font("Arial"));}
    @FXML
    private void setTimes(ActionEvent e) {textArea.setFont(Font.font("Times New Roman"));}
    @FXML
    private void setVerdana(ActionEvent e) {textArea.setFont(Font.font("Verdana"));}
    @FXML
    private void setHelvetica(ActionEvent e) {textArea.setFont(Font.font("Helvetica"));}
    @FXML
    private void setCalibri(ActionEvent e) {textArea.setFont(Font.font("Calibri"));}

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
        App.setRoot("library");
    }
}
