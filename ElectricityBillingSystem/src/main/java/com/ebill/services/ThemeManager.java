package com.ebill.services;

import javafx.scene.Scene;
import java.util.Objects;

public class ThemeManager {
    private static ThemeManager instance;
    private boolean isDarkTheme = false;

    private ThemeManager() {}

    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public void toggleTheme(Scene scene) {
        isDarkTheme = !isDarkTheme;
        applyTheme(scene);
    }

    public void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        String themePath;
        if (isDarkTheme) {
            themePath = "/css/dark-theme.css";
        } else {
            themePath = "/css/light-theme.css";
        }
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(themePath)).toExternalForm());
    }
    
    public boolean isDarkTheme() {
        return isDarkTheme;
    }
}