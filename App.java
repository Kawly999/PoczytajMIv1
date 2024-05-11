package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.controller.PrimaryController;
import org.example.controller.SceneManager;
import org.example.controller.SecondaryController;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Stage primaryStage;
    private static Scene scene;
    private static SceneManager sceneManager;

    @Override
    public void start(Stage stage) throws IOException {
        Set<String> fxmlStringSet = Set.of("primary.fxml", "library.fxml", "logging.fxml", "Registration.fxml", "fileBar.fxml", "reading.fxml", "folderBar.fxml");
        sceneManager = SceneManager.getInstance(fxmlStringSet);

        primaryStage = stage;
        scene = sceneManager.getScene("logging");

        stage.setScene(scene);
        enableDragWindow(stage, scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene = SceneManager.getInstance().getScene(fxml);
        primaryStage.setScene(scene);
        enableDragWindow(primaryStage, scene);
    }
    public static void closeStage() {primaryStage.close();}
    public static void minimizeStage() {primaryStage.setIconified(true);}
    public static void setFullScreenStage() {primaryStage.setFullScreen(!primaryStage.isFullScreen());}

    private static void enableDragWindow(Stage stage, Scene scene) {
        final double[] xOffset = new double[1];
        final double[] yOffset = new double[1];

        scene.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        });
    }
    public static void main(String[] args) {
        launch();
    }

}