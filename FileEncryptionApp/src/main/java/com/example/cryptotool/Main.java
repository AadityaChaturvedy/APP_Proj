package com.example.cryptotool;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Crypter Pro");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(900);

        UIController uiController = new UIController(primaryStage);
        BorderPane root = uiController.getRoot();

        Scene scene = new Scene(root, 900, 950);

        // Load the default theme from settings
        String defaultTheme = new AppSettings().getDefaultTheme();
        scene.getStylesheets().add(getClass().getResource("/styles/" + defaultTheme).toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}