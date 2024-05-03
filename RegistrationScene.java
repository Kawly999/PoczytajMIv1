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

public class RegistrationScene implements Controller {
    @FXML
    public TextField nameTextField;
    @FXML
    public TextField lastNameTextField;
    @FXML
    public TextField EmailTextField;
    @FXML
    public TextField PasswordTextField;
    @FXML
    public TextField RepeatPasswordTextField;
    @FXML
    public Button createAccountButton;
    @FXML
    public Hyperlink haveAccountButton;
    @FXML
    private Label errorMassageFillLabel;
    private final DatabaseConnector dbConnector = new DatabaseConnector();
    @FXML
    public void changeToLoggingScene(ActionEvent e) throws IOException {
        App.setRoot("logging");
    }
    @FXML
    public void readFormValues(ActionEvent e) throws IOException {
        String name = nameTextField.getText();
        String lastName = lastNameTextField.getText();
        String email = EmailTextField.getText();
        String password = PasswordTextField.getText();
        String repeatPassword = RepeatPasswordTextField.getText();
        PreparedStatement psCheckUserExists = null;
        PreparedStatement psInsert = null;
        ResultSet resultSet = null;
        if (name.isEmpty()
                || lastName.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || repeatPassword.isEmpty()
        ) {
            errorMassageFillLabel.setText("Wszystkie pola muszą być wypełnione.");
            errorMassageFillLabel.setVisible(true);
        }
        else if (!password.equals(repeatPassword)) {
            // Hasła nie są takie same, pokazujemy błąd
            errorMassageFillLabel.setText("Hasła nie są identyczne.");
            errorMassageFillLabel.setVisible(true);
        }
        else {
            try (Connection connection = dbConnector.connect()) {
                psCheckUserExists = connection.prepareStatement("Select * FROM users WHERE email = ?");
                psCheckUserExists.setString(1, email);
                resultSet = psCheckUserExists.executeQuery();

                if (resultSet.isBeforeFirst()) {
                    System.out.println("Taki email już istnieje");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Nie możesz użyć tego adresu email.");
                } else {
                    psInsert = connection.prepareStatement("INSERT INTO users (first_name, last_name, password_hash, email) VALUES (?, ?, ?, ?)");
                    psInsert.setString(1, name);
                    psInsert.setString(2, lastName);
                    psInsert.setString(3, password);
                    psInsert.setString(4, email);
                    psInsert.executeUpdate();
                    App.setRoot("logging");
                }

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
