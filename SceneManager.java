package org.example.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SceneManager {
    private final Map<String, Scene> scenes = new HashMap<>();
    private final Map<String, Controller> controllers = new HashMap<>();
    private static SceneManager instance;

    private SceneManager(Set<String> fxmlSet) throws IOException {
        for (String file : fxmlSet) {
            loadScene(file);
        }
    }
    private void loadScene(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxml));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Controller controller = loader.getController();
        // ciag znaków od / do .
        String sceneId = fxml.substring(fxml.lastIndexOf("/") + 1, fxml.lastIndexOf('.'));
        scenes.put(sceneId, scene);
        controllers.put(sceneId, controller);
    }
    public static SceneManager getInstance() {
        return instance;
    }
    public static SceneManager getInstance(Set<String> fxmlSet) throws IOException {
        if (instance == null) {
            instance = new SceneManager(fxmlSet);
        }
        return instance;
    }
    public Scene getScene(String filename) {
        return scenes.get(filename);
    }
    public Controller getController(String filename) {
        return controllers.get(filename);
    }
}
