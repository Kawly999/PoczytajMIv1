package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.App;
import org.example.model.CloudStorageManager;
import org.example.model.DatabaseConnector;
import org.example.model.SharedVariables;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LoggingScene implements Controller {
    private final DatabaseConnector dbConnector = new DatabaseConnector();
    private final SharedVariables sharedVariables = SharedVariables.getInstance();
    private final CloudStorageManager cloudStorageManager = CloudStorageManager.getInstance();
    @FXML
    public Button logInButton;
    @FXML
    public TextField emailTextField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Label errorMassageLabel;
    @FXML
    public Hyperlink noAccHyperLink;
    private String email = "";

    @FXML
    public void readLogInValues(ActionEvent e) throws IOException {
        email = emailTextField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorMassageLabel.setText("Pola nie mogą być puste!");
            errorMassageLabel.setVisible(true);
        } else {
            LogIn();
        }
    }
    private void LogIn() throws IOException {
        ResultSet resultSet;
        PreparedStatement psCheckUserExists;
        try (Connection connection = dbConnector.connect()) {
            psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE email = ?");
            psCheckUserExists.setString(1, email);
            resultSet = psCheckUserExists.executeQuery();
            if (resultSet.isBeforeFirst()) {
                // dodawanie pobranych plików z GCS
                SecondaryController sc = (SecondaryController) SceneManager.getInstance().getController("library");
                List<String> downloadedFilePaths = cloudStorageManager.downloadFilesWithUserId();
                for (String filePath : downloadedFilePaths) {
                    File file = new File(filePath);
                    sc.addFileBarReadingGCS(file);
                }
                // zmiana sceny na główną
                changeScene("primary");
            } else {
                errorMassageLabel.setText("Błędny email lub hasło!");
                errorMassageLabel.setVisible(true);
            }
            setEmailInSharedVariables();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    @FXML
    public void changeToRegistrationScene() throws IOException {changeScene("Registration");}
    public void setEmailInSharedVariables() {sharedVariables.setEmail(email);}
    private static void changeScene(String sceneName) throws IOException {App.setRoot(sceneName);}
}
