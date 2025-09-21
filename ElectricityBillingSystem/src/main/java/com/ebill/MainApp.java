package com.ebill;

import com.ebill.services.LocalizationService;
import com.ebill.services.ThemeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        LocalizationService.getInstance().loadLanguage("en");
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/LoginView.fxml")));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 400);
        ThemeManager.getInstance().applyTheme(scene);
        primaryStage.setTitle("Electricity Billing System Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}