package com.example.cryptotool;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AlertFactory {

    public static void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public static void showSuccessAlert(String title, String content) {
        showAlert(Alert.AlertType.INFORMATION, title, content);
    }

    public static void showAboutDialog() {
        showAlert(Alert.AlertType.INFORMATION, "About Crypter Pro",
                "Version: 3.0\n\n" +
                        "This tool uses industry-standard AES-256-GCM encryption to secure your files. " +
                        "The encryption key is derived from your password and an optional key file using PBKDF2.");
    }
}

