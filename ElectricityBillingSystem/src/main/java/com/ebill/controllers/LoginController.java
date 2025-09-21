package com.ebill.controllers;

import com.ebill.services.LocalizationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    @FXML private Label titleLabel, usernameLabel, passwordLabel, errorLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    private final LocalizationService localizationService = LocalizationService.getInstance();

    @FXML
    public void initialize() {
        bindLocalization();
        errorLabel.setVisible(false);
    }

    private void bindLocalization() {
        titleLabel.textProperty().bind(localizationService.getProperty("ui.login_title"));
        usernameLabel.textProperty().bind(localizationService.getProperty("ui.username"));
        passwordLabel.textProperty().bind(localizationService.getProperty("ui.password"));
        loginButton.textProperty().bind(localizationService.getProperty("ui.login_button"));
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if ("admin".equals(username) && "admin".equals(password)) {
            try {
                openAdminDashboard();
            } catch (IOException e) {
                e.printStackTrace();
                errorLabel.setText("Failed to load dashboard.");
                errorLabel.setVisible(true);
            }
        } else {
            errorLabel.textProperty().bind(localizationService.getProperty("ui.login_error"));
            errorLabel.setVisible(true);
        }
    }

    private void openAdminDashboard() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/AdminDashboard.fxml")));
        Parent adminRoot = loader.load();

        Stage stage = (Stage) loginButton.getScene().getWindow();
        Scene scene = new Scene(adminRoot, 1200, 800);

        AdminDashboardController controller = loader.getController();
        controller.setScene(scene);

        stage.setScene(scene);
        stage.setTitle("Admin Dashboard - Electricity Billing System");
        stage.centerOnScreen();
    }
}