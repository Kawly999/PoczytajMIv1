package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.App;
import org.example.model.DatabaseConnector;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoggingScene implements Controller {
    private final DatabaseConnector dbConnector = new DatabaseConnector();
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

    public void changeToRegistrationScene(ActionEvent e) throws IOException {
        App.setRoot("Registration");
    }
    @FXML
    public void readLogInValues(ActionEvent e) throws IOException {
        String email = emailTextField.getText();
        String password = passwordField.getText();
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;

        if (email.isEmpty() || password.isEmpty()) {
            errorMassageLabel.setText("Pola nie mogą być puste!");
            errorMassageLabel.setVisible(true);
        } else {
            try (Connection connection = dbConnector.connect()) {
                psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE email = ?");
                psCheckUserExists.setString(1, email);
                resultSet = psCheckUserExists.executeQuery();
                if (resultSet.isBeforeFirst()) {
                    App.setRoot("primary");
                } else {
                    errorMassageLabel.setText("Błędny email lub hasło!");
                    errorMassageLabel.setVisible(true);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }



}
