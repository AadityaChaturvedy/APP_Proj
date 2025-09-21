package com.example.cryptotool;

import java.util.prefs.Preferences;

public class AppSettings {

    private static final String DEFAULT_THEME = "DEFAULT_THEME";
    private static final String DEFAULT_SECURE_DELETE = "DEFAULT_SECURE_DELETE";
    private final Preferences prefs;

    public AppSettings() {
        this.prefs = Preferences.userNodeForPackage(AppSettings.class);
    }

    public String getDefaultTheme() {
        return prefs.get(DEFAULT_THEME, "dark-theme.css");
    }

    public void setDefaultTheme(String theme) {
        prefs.put(DEFAULT_THEME, theme);
    }

    public boolean isDefaultSecureDelete() {
        return prefs.getBoolean(DEFAULT_SECURE_DELETE, false);
    }

    public void setDefaultSecureDelete(boolean enabled) {
        prefs.putBoolean(DEFAULT_SECURE_DELETE, enabled);
    }
}
