package com.example.edutrack.utils;

import com.example.edutrack.App;
import javafx.scene.Scene;
import java.util.prefs.Preferences;

public class ThemeManager {
    private static final String THEME_PREF_KEY = "app_theme";
    public enum Theme { LIGHT, DARK }

    public static void applyTheme(Scene scene, Theme theme) {
        // Clear all previous stylesheets to prevent conflicts
        scene.getStylesheets().clear();

        // Always load the base stylesheet which now contains the light theme variables
        String baseCss = App.class.getResource("css/styles.css").toExternalForm();
        scene.getStylesheets().add(baseCss);

        // If the dark theme is selected, add the override stylesheet
        if (theme == Theme.DARK) {
            String darkThemeCss = App.class.getResource("css/dark-theme.css").toExternalForm();
            scene.getStylesheets().add(darkThemeCss);
        }

        saveThemePreference(theme);
    }

    public static void saveThemePreference(Theme theme) {
        Preferences prefs = Preferences.userNodeForPackage(App.class);
        prefs.put(THEME_PREF_KEY, theme.name());
    }

    public static Theme loadThemePreference() {
        Preferences prefs = Preferences.userNodeForPackage(App.class);
        String themeName = prefs.get(THEME_PREF_KEY, Theme.LIGHT.name());
        return Theme.valueOf(themeName);
    }
}