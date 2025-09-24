package com.example.edutrack.controllers;

import com.example.edutrack.App;
import com.example.edutrack.models.User;
import com.example.edutrack.services.UserService;
import com.example.edutrack.utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = userService.authenticate(username, password);

        if (user != null) {
            // Successful login, open main application window
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/MainView.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

                Stage mainStage = new Stage();
                mainStage.setTitle("EduTrack - Student Management Dashboard");
                mainStage.setScene(scene);
                mainStage.setMinWidth(1000);
                mainStage.setMinHeight(700);

                // Close login stage
                Stage loginStage = (Stage) usernameField.getScene().getWindow();
                loginStage.close();

                mainStage.show();
            } catch (IOException e) {
                e.printStackTrace();
                AlertUtils.showError("Application Error", "Failed to load the main application view.");
            }
        } else {
            // Failed login
            AlertUtils.showError("Login Failed", "Invalid username or password.");
        }
    }
}