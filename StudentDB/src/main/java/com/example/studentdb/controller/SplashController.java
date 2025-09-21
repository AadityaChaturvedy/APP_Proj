package com.example.studentdb.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SplashController {
    @FXML private Label splashLabel;

    public void initialize() {
        // Fade in the text
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), splashLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Fade out the text
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), splashLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(1.0));

        fadeOut.setOnFinished(e -> showMain());

        fadeIn.play();
        fadeIn.setOnFinished(e -> fadeOut.play());
    }

    private void showMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            Stage stage = (Stage) splashLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}