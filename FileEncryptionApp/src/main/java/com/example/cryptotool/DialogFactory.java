package com.example.cryptotool;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

/**
 * A utility class for creating various dialog boxes used in the application.
 */
public class DialogFactory {

    /**
     * Creates and displays a modal dialog to securely ask the user for a decryption password.
     *
     * @param owner The parent stage.
     * @return An Optional containing the password as a char array if submitted, or an empty Optional if cancelled.
     */
    public static Optional<char[]> createDecryptionDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Enter Decryption Password");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        layout.getChildren().add(new Label("Please enter the password to decrypt the selected files:"));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        layout.getChildren().add(passwordField);

        Button okButton = new Button("Decrypt");
        Button cancelButton = new Button("Cancel");

        final char[][] passwordHolder = new char[1][];

        okButton.setOnAction(e -> {
            passwordHolder[0] = passwordField.getText().toCharArray();
            dialog.close();
        });
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10, okButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        layout.getChildren().add(buttonBox);

        passwordField.requestFocus();

        Scene scene = new Scene(layout);
        dialog.setScene(scene);
        dialog.showAndWait();

        return Optional.ofNullable(passwordHolder[0]);
    }

    /**
     * Creates a generic, non-modal window to display information.
     * Used for placeholder actions like Settings, Help, etc.
     *
     * @param owner The parent stage.
     * @param title The title of the window.
     * @param contentText The main text content to display.
     */
    public static void createInfoDialog(Stage owner, String title, String contentText) {
        Stage infoStage = new Stage();
        infoStage.initOwner(owner);
        infoStage.setTitle(title);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);

        Text content = new Text(contentText);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> infoStage.close());

        layout.getChildren().addAll(content, closeButton);

        Scene scene = new Scene(layout, 350, 150);
        infoStage.setScene(scene);
        infoStage.show();
    }
}
