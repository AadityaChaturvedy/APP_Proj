package com.example.cryptotool;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class FileStatusItem {
    private final File file;
    private final StringProperty fileName;
    private final StringProperty fileSize;
    private final StringProperty status;

    public FileStatusItem(File file) {
        this.file = file;
        this.fileName = new SimpleStringProperty(file.getName());
        this.fileSize = new SimpleStringProperty(formatFileSize(file.length()));
        this.status = new SimpleStringProperty("Ready");
    }

    public File getFile() { return file; }
    public String getFileName() { return fileName.get(); }
    public StringProperty fileNameProperty() { return fileName; }
    public String getFileSize() { return fileSize.get(); }
    public StringProperty fileSizeProperty() { return fileSize; }
    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }
    public StringProperty statusProperty() { return status; }

    // Helper to format file size into KB, MB, etc.
    private static String formatFileSize(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }
}
