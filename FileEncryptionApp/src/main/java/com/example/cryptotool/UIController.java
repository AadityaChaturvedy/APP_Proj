package com.example.cryptotool;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class UIController {

    private static final Logger logger = LoggerFactory.getLogger(UIController.class);

    private final Stage primaryStage;
    private final CryptoService cryptoService;
    private final UIBuilder uiBuilder;
    private final AppSettings appSettings;
    private final BorderPane root;

    private final ObservableList<FileStatusItem> fileList = FXCollections.observableArrayList();
    private File keyFile;
    private Task<Void> currentTask;
    private final AtomicBoolean cancellationFlag = new AtomicBoolean(false);

    public UIController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.cryptoService = new CryptoService();
        this.appSettings = new AppSettings();
        this.uiBuilder = new UIBuilder(this);
        this.root = uiBuilder.buildUI();
        uiBuilder.getFileTableView().setItems(fileList);
        logger.info("Application started. UI Initialized.");
    }

    public BorderPane getRoot() { return root; }
    public AppSettings getAppSettings() { return appSettings; }

    // --- Header/Menu Actions ---
    public void showHome() { DialogFactory.createInfoDialog(primaryStage, "Home", "This is the main application screen."); }
    public void showSettings() { DialogFactory.createInfoDialog(primaryStage, "Settings", "Application settings would be configured here."); }
    public void showHelp() { DialogFactory.createInfoDialog(primaryStage, "Help", "Find documentation and support information here."); }
    public void manageKeys() { DialogFactory.createInfoDialog(primaryStage, "Manage Keys", "Key management features would be available here."); }
    public void showProfile() { DialogFactory.createInfoDialog(primaryStage, "Profile", "User profile information would be displayed here."); }

    // --- File Handling ---
    public void addFiles() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Files to Add");
        List<File> files = fc.showOpenMultipleDialog(primaryStage);
        if (files != null) handleFileSelection(files);
    }

    public void handleFileSelection(List<File> files) {
        List<File> allFiles = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                collectFiles(file, allFiles);
            } else {
                allFiles.add(file);
            }
        }
        allFiles.forEach(file -> fileList.add(new FileStatusItem(file)));
        logger.info("{} files added to the list.", allFiles.size());
    }

    public void removeSelectedFiles() {
        TableView<FileStatusItem> tableView = uiBuilder.getFileTableView();
        fileList.removeAll(tableView.getSelectionModel().getSelectedItems());
        logger.info("Selected files removed from the list.");
    }

    public void clearSelectedFiles() {
        fileList.clear();
        logger.info("File list cleared.");
    }

    public void selectKeyFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Key File (Optional)");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            this.keyFile = file;
            uiBuilder.updateKeyFileLabel(file.getName());
            logger.info("Key file selected: {}", file.getAbsolutePath());
        }
    }

    // --- Main Processing Logic ---
    public void processFiles(boolean isEncrypting) {
        char[] password = uiBuilder.getPassword();
        boolean secureDelete = isEncrypting && uiBuilder.isSecureDeleteEnabled();

        if (!validateInputsForEncryption(password)) return;

        startTask(new ArrayList<>(fileList), password, keyFile, true, secureDelete);
    }

    public void showDecryptionDialog() {
        if (fileList.isEmpty()) {
            AlertFactory.showAlert(Alert.AlertType.WARNING, "No Files Selected", "Please add one or more files to decrypt.");
            return;
        }

        Optional<char[]> passwordOpt = DialogFactory.createDecryptionDialog(primaryStage);
        passwordOpt.ifPresent(password -> {
            startTask(new ArrayList<>(fileList), password, keyFile, false, false);
        });
    }

    private void startTask(List<FileStatusItem> items, char[] password, File keyFile, boolean isEncrypting, boolean secureDelete) {
        logger.info("Starting a '{}' operation for {} files.", isEncrypting ? "ENCRYPT" : "DECRYPT", items.size());
        cancellationFlag.set(false);
        currentTask = createTask(items, password, keyFile, isEncrypting, secureDelete);

        uiBuilder.getProgressBar().progressProperty().bind(currentTask.progressProperty());
        uiBuilder.getStatusLabel().textProperty().bind(currentTask.messageProperty());

        currentTask.setOnRunning(e -> uiBuilder.setUiDisabled(true));
        currentTask.setOnSucceeded(e -> onTaskFinished(true, "Operation Complete", "All files were processed successfully."));
        currentTask.setOnCancelled(e -> onTaskFinished(false, "Operation Cancelled", "The operation was cancelled by the user."));
        currentTask.setOnFailed(e -> {
            Throwable ex = currentTask.getException();
            String msg = (ex != null && ex.getCause() != null) ? ex.getCause().getMessage() : "An unknown error occurred.";
            onTaskFinished(false, "Operation Failed", msg);
            logger.error("Task failed with exception:", ex);
        });

        new Thread(currentTask).start();
    }

    public void cancelOperation() {
        if (currentTask != null && currentTask.isRunning()) {
            cancellationFlag.set(true);
            logger.warn("Cancel button clicked. Flagging task for cancellation.");
        }
    }

    // --- Helper Methods ---
    private boolean validateInputsForEncryption(char[] pass) {
        if (fileList.isEmpty()) {
            AlertFactory.showAlert(Alert.AlertType.WARNING, "No Files Selected", "Please add one or more files to process.");
            return false;
        }
        if (pass.length < 8) {
            AlertFactory.showAlert(Alert.AlertType.WARNING, "Weak Password", "Password must be at least 8 characters.");
            return false;
        }
        return true;
    }

    private void onTaskFinished(boolean success, String title, String message) {
        uiBuilder.setUiDisabled(false);
        uiBuilder.getProgressBar().progressProperty().unbind();
        uiBuilder.getStatusLabel().textProperty().unbind();
        uiBuilder.getProgressBar().setProgress(0);
        uiBuilder.getStatusLabel().setText("Status: " + title);

        if (success) {
            logger.info("Task finished successfully.");
            AlertFactory.showSuccessAlert(title, message);
            clearSelectedFiles();
        } else {
            logger.warn("Task finished with cancellation or failure.");
            AlertFactory.showAlert(Alert.AlertType.WARNING, title, message);
        }
        uiBuilder.clearPasswordFields();
    }

    private void collectFiles(File directory, List<File> files) {
        File[] dirFiles = directory.listFiles();
        if (dirFiles != null) {
            for (File file : dirFiles) {
                if (file.isDirectory()) collectFiles(file, files);
                else files.add(file);
            }
        }
    }

    private Task<Void> createTask(List<FileStatusItem> items, char[] password, File keyFile, boolean isEncrypting, boolean secureDelete) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                long totalFiles = items.size();
                for (int i = 0; i < totalFiles; i++) {
                    if (cancellationFlag.get()) {
                        updateMessage("Status: Cancelling...");
                        break;
                    }

                    final int fileIndex = i;
                    FileStatusItem currentItem = items.get(fileIndex);
                    File inputFile = currentItem.getFile();
                    String outputFileName = getOutputFileName(inputFile.getName(), isEncrypting);
                    File outputFile = new File(inputFile.getParent(), outputFileName);

                    try {
                        String operation = isEncrypting ? "Encrypting" : "Decrypting";
                        updateMessage(String.format("Status: %s (%d/%d): %s", operation, fileIndex + 1, totalFiles, inputFile.getName()));
                        currentItem.setStatus(operation + "...");

                        if (isEncrypting) {
                            cryptoService.encrypt(password, keyFile, inputFile, outputFile, cancellationFlag,
                                    (p, t) -> updateProgress(fileIndex + ((double)p/t), totalFiles));
                            if (secureDelete && !cancellationFlag.get()) cryptoService.secureDelete(inputFile);
                        } else {
                            cryptoService.decrypt(password, keyFile, inputFile, outputFile, cancellationFlag,
                                    (p, t) -> updateProgress(fileIndex + ((double)p/t), totalFiles));
                        }

                        if (cancellationFlag.get()){
                            currentItem.setStatus("Cancelled");
                            if(outputFile.exists()) outputFile.delete();
                        } else {
                            currentItem.setStatus("Success");
                        }

                    } catch (Exception e) {
                        currentItem.setStatus("Failed");
                        logger.error("Processing failed for file: {}", inputFile.getAbsolutePath(), e);
                        throw e;
                    }
                }
                updateMessage("Status: " + (cancellationFlag.get() ? "Cancelled!" : "Done!"));
                return null;
            }
        };
    }

    private String getOutputFileName(String inputName, boolean isEncrypting) {
        return isEncrypting ? inputName + ".enc" : (inputName.toLowerCase().endsWith(".enc") ? inputName.substring(0, inputName.length() - 4) : inputName + ".dec");
    }
}

