package com.example.cryptotool;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;

public class UIBuilder {

    private final UIController controller;
    private BorderPane root;
    private TableView<FileStatusItem> fileTableView;
    private Label keyFileLabel;
    private PasswordField passwordField;
    private CheckBox secureDeleteCheckbox;
    private ProgressBar progressBar;
    private Label statusLabel;
    private Button cancelButton;
    private Node mainContent;

    public UIBuilder(UIController controller) {
        this.controller = controller;
    }

    public BorderPane buildUI() {
        root = new BorderPane();
        root.setId("root");
        root.setTop(createHeader());
        mainContent = createMainContent();
        root.setCenter(mainContent);
        return root;
    }

    private Node createHeader() {
        Label homeLabel = new Label("Home");
        homeLabel.setOnMouseClicked(e -> controller.showHome());
        Label settingsLabel = new Label("Settings");
        settingsLabel.setOnMouseClicked(e -> controller.showSettings());
        Label helpLabel = new Label("Help");
        helpLabel.setOnMouseClicked(e -> controller.showHelp());

        HBox menuBar = new HBox(15, homeLabel, settingsLabel, helpLabel);
        menuBar.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button manageKeysButton = createIconButton("Manage Keys", createIcon("M12 11c1.657 0 3-1.343 3-3V6a3 3 0 00-6 0v2c0 1.657 1.343 3 3 3z"));
        manageKeysButton.setOnAction(e -> controller.manageKeys());
        Button profileButton = createIconButton("Profile", createIcon("M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"));
        profileButton.setOnAction(e -> controller.showProfile());

        HBox userBar = new HBox(15, manageKeysButton, profileButton);
        userBar.setAlignment(Pos.CENTER_RIGHT);

        HBox header = new HBox(menuBar, spacer, userBar);
        header.getStyleClass().add("header");
        return header;
    }

    private Node createMainContent() {
        Node fileSection = createTitledSection("File Selection", createFileArea());
        Node securitySection = createTitledSection("Security & Options", createSecurityArea());
        Node executeSection = createTitledSection("Execute", createActionArea());
        Node progressSection = createTitledSection("Progress", createProgressArea());

        VBox layout = new VBox(30, fileSection, securitySection, executeSection, progressSection);
        layout.setPadding(new Insets(30, 50, 50, 50));
        layout.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        return scrollPane;
    }

    private Node createTitledSection(String title, Node content) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");
        VBox section = new VBox(20, titleLabel, content);
        section.getStyleClass().add("section-box");
        return section;
    }

    private Node createFileArea() {
        VBox dropZone = new VBox(15);
        dropZone.setAlignment(Pos.CENTER);
        dropZone.getStyleClass().add("drop-zone");
        dropZone.getChildren().add(new Label("Drag & Drop Files or Folders Here"));
        setupDragAndDrop(dropZone);

        fileTableView = new TableView<>();
        fileTableView.setPrefHeight(250);
        fileTableView.setPlaceholder(new Label("Your selected files will appear here"));

        TableColumn<FileStatusItem, String> nameCol = new TableColumn<>("File Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        nameCol.setPrefWidth(350);

        TableColumn<FileStatusItem, String> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("fileSize"));

        TableColumn<FileStatusItem, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        fileTableView.getColumns().addAll(nameCol, sizeCol, statusCol);
        fileTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileTableView.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) controller.removeSelectedFiles();
        });

        Button addButton = createIconButton("Add Files", createIcon("M12 5v14m-7-7h14"));
        addButton.setOnAction(e -> controller.addFiles());

        Button removeButton = createIconButton("Remove Selected", createIcon("M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"));
        removeButton.setOnAction(e -> controller.removeSelectedFiles());

        Button clearButton = createIconButton("Clear All", createIcon("M6 18L18 6M6 6l12 12"));
        clearButton.setOnAction(e -> controller.clearSelectedFiles());

        HBox buttonBox = new HBox(15, addButton, removeButton, clearButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        VBox layout = new VBox(20, dropZone, fileTableView, buttonBox);
        return layout;
    }

    private Node createSecurityArea() {
        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(20);

        keyFileLabel = new Label("None");
        Button selectKeyBtn = new Button("Select...");
        selectKeyBtn.setOnAction(e -> controller.selectKeyFile());
        HBox keyFileBox = new HBox(10, keyFileLabel, selectKeyBtn);
        keyFileBox.setAlignment(Pos.CENTER_LEFT);
        grid.add(new Label("Key File (Optional):"), 0, 0);
        grid.add(keyFileBox, 1, 0);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password for encryption");
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);

        secureDeleteCheckbox = new CheckBox("Securely delete original file after encryption");
        grid.add(secureDeleteCheckbox, 1, 2);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(70);
        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    private Node createActionArea() {
        Button encryptButton = new Button("Encrypt");
        encryptButton.setId("encrypt-button");
        encryptButton.setOnAction(e -> controller.processFiles(true));

        Button decryptButton = new Button("Decrypt");
        decryptButton.setId("decrypt-button");
        decryptButton.setOnAction(e -> controller.showDecryptionDialog()); // <-- UPDATED ACTION

        HBox actionButtons = new HBox(20, encryptButton, decryptButton);
        actionButtons.setAlignment(Pos.CENTER_LEFT);
        return actionButtons;
    }

    private Node createProgressArea() {
        statusLabel = new Label("Status: Ready");
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        cancelButton = new Button("Cancel");
        cancelButton.setVisible(false);
        cancelButton.setOnAction(e -> controller.cancelOperation());

        VBox progressBox = new VBox(10, statusLabel, progressBar, cancelButton);
        progressBox.setAlignment(Pos.CENTER);
        progressBox.getStyleClass().add("progress-box");
        return progressBox;
    }

    private void setupDragAndDrop(Node node) {
        node.setOnDragOver(e -> {
            if (e.getDragboard().hasFiles()) e.acceptTransferModes(TransferMode.COPY);
            e.consume();
        });
        node.setOnDragDropped(e -> {
            if (e.getDragboard().hasFiles()) controller.handleFileSelection(e.getDragboard().getFiles());
            e.setDropCompleted(true);
            e.consume();
        });
    }

    private Node createIcon(String path) {
        SVGPath svg = new SVGPath();
        svg.setContent(path);
        svg.getStyleClass().add("icon");
        return svg;
    }

    private Button createIconButton(String text, Node icon) {
        Button button = new Button(text);
        button.setGraphic(icon);
        return button;
    }

    public void clearPasswordFields() {
        passwordField.clear();
    }

    // --- Public Getters for the Controller ---
    public TableView<FileStatusItem> getFileTableView() { return fileTableView; }
    public void updateKeyFileLabel(String fileName) { keyFileLabel.setText(fileName == null ? "None" : fileName); }
    public char[] getPassword() { return passwordField.getText().toCharArray(); }
    public boolean isSecureDeleteEnabled() { return secureDeleteCheckbox.isSelected(); }
    public ProgressBar getProgressBar() { return progressBar; }
    public Label getStatusLabel() { return statusLabel; }
    public void setUiDisabled(boolean disabled) {
        mainContent.setDisable(disabled);
        cancelButton.setVisible(disabled);
    }
}

