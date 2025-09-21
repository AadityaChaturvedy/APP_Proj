package com.ebill.controllers;

import com.ebill.models.Bill;
import com.ebill.services.DataService;
import com.ebill.services.LocalizationService;
import com.ebill.services.ThemeManager;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class AdminDashboardController {

    private record LanguageItem(String displayName, String code) {
    }

    @FXML private TableView<Bill> billTableView;
    @FXML private TableColumn<Bill, Integer> meterNumberCol;
    @FXML private TableColumn<Bill, String> customerNameCol;
    @FXML private TableColumn<Bill, String> cityCol;
    @FXML private TableColumn<Bill, Integer> unitsCol;
    @FXML private TableColumn<Bill, Double> amountCol;
    @FXML private TableColumn<Bill, String> statusCol;
    @FXML private TextField searchField;
    @FXML private Button addButton, editButton, deleteButton;
    @FXML private ToggleButton themeToggleButton;
    @FXML private ComboBox<LanguageItem> languageComboBox;
    @FXML private TitledPane chatbotPane;
    @FXML private Label dashboardTitle;

    private final DataService dataService = DataService.getInstance();
    private final LocalizationService loc = LocalizationService.getInstance();
    private final ThemeManager themeManager = ThemeManager.getInstance();
    private FilteredList<Bill> filteredData;
    private Scene scene;

    public void setScene(Scene scene) {
        this.scene = scene;
        themeManager.applyTheme(this.scene);
        setupThemeToggle();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSearchFilter();
        setupLanguageSwitcher();
        bindLocalization();
        updateButtonStates();

        billTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> updateButtonStates());

        addButton.setGraphic(new FontIcon("fas-plus-circle"));
        editButton.setGraphic(new FontIcon("fas-edit"));
        deleteButton.setGraphic(new FontIcon("fas-trash-alt"));
    }
    
    private void setupLanguageSwitcher() {
        // Note: The LanguageItem's displayName is just a placeholder here.
        // The actual text is set by the cell factory using JSON keys.
        LanguageItem en = new LanguageItem("English", "en");
        LanguageItem hi = new LanguageItem("Hindi", "hi");
        LanguageItem ta = new LanguageItem("Tamil", "ta");

        languageComboBox.setItems(FXCollections.observableArrayList(en, hi, ta));

        Callback<ListView<LanguageItem>, ListCell<LanguageItem>> cellFactory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(LanguageItem item, boolean empty) {
                super.updateItem(item, empty);
                textProperty().unbind(); // Always unbind first
                if (empty || item == null) {
                    setText(null);
                } else {
                    textProperty().bind(loc.getProperty("languages." + item.code()));
                }
            }
        };

        languageComboBox.setCellFactory(cellFactory);
        languageComboBox.setButtonCell(cellFactory.call(null));

        languageComboBox.getSelectionModel().selectFirst();

        languageComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loc.loadLanguage(newVal.code());
            }
        });
    }

    private void setupTableColumns() {
        meterNumberCol.setCellValueFactory(new PropertyValueFactory<>("meterNumber"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        cityCol.setCellValueFactory(new PropertyValueFactory<>("city"));
        unitsCol.setCellValueFactory(new PropertyValueFactory<>("unitsConsumed"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("billAmount"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        amountCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("₹ %.2f", price));
                }
            }
        });
    }

    private void setupSearchFilter() {
        filteredData = new FilteredList<>(dataService.getBills(), b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(bill -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (bill.getCustomerName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(bill.getMeterNumber()).contains(lowerCaseFilter)) {
                    return true;
                } else return bill.getCity().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Bill> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(billTableView.comparatorProperty());
        billTableView.setItems(sortedData);
    }

    private void setupThemeToggle() {
        themeToggleButton.setSelected(themeManager.isDarkTheme());
        updateThemeIcon();
        themeToggleButton.setOnAction(event -> {
            themeManager.toggleTheme(scene);
            updateThemeIcon();
        });
    }

    private void updateThemeIcon() {
        if (themeManager.isDarkTheme()) {
            themeToggleButton.setGraphic(new FontIcon("fas-sun"));
        } else {
            themeToggleButton.setGraphic(new FontIcon("fas-moon"));
        }
    }

    private void bindLocalization() {
        dashboardTitle.textProperty().bind(loc.getProperty("ui.dashboard_title"));
        searchField.promptTextProperty().bind(loc.getProperty("ui.search_placeholder"));
        addButton.textProperty().bind(loc.getProperty("ui.add_button"));
        editButton.textProperty().bind(loc.getProperty("ui.edit_button"));
        deleteButton.textProperty().bind(loc.getProperty("ui.delete_button"));
        chatbotPane.textProperty().bind(loc.getProperty("ui.chatbot_title"));
        meterNumberCol.textProperty().bind(loc.getProperty("table.meter_number"));
        customerNameCol.textProperty().bind(loc.getProperty("table.customer_name"));
        cityCol.textProperty().bind(loc.getProperty("table.city"));
        unitsCol.textProperty().bind(loc.getProperty("table.units"));
        amountCol.textProperty().bind(loc.getProperty("table.amount"));
        statusCol.textProperty().bind(loc.getProperty("table.status"));
    }

    private void updateButtonStates() {
        boolean itemSelected = billTableView.getSelectionModel().getSelectedItem() != null;
        editButton.setDisable(!itemSelected);
        deleteButton.setDisable(!itemSelected);
    }

    @FXML
    private void handleAddBill() {
        showBillForm(null);
    }

    @FXML
    private void handleEditBill() {
        Bill selectedBill = billTableView.getSelectionModel().getSelectedItem();
        if (selectedBill != null) {
            showBillForm(selectedBill);
        }
    }

    @FXML
    private void handleDeleteBill() {
        Bill selectedBill = billTableView.getSelectionModel().getSelectedItem();
        if (selectedBill != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.getDialogPane().getStylesheets().addAll(scene.getStylesheets());

            alert.titleProperty().bind(loc.getProperty("dialog.delete_title"));
            alert.headerTextProperty().bind(loc.getProperty("dialog.delete_header"));
            String content = loc.getString("dialog.delete_content").replace("{customerName}", selectedBill.getCustomerName());
            alert.setContentText(content);
            
            ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).textProperty().bind(loc.getProperty("dialog.confirm_button"));
            ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).textProperty().bind(loc.getProperty("dialog.cancel_button"));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                dataService.deleteBill(selectedBill);
            }
        }
    }

    private void showBillForm(Bill bill) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/CustomerForm.fxml")));
            Parent page = loader.load();
            Stage dialogStage = new Stage();
            if (bill == null) {
                dialogStage.titleProperty().bind(loc.getProperty("form.add_title"));
            } else {
                dialogStage.titleProperty().bind(loc.getProperty("form.edit_title"));
            }
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(addButton.getScene().getWindow());
            Scene formScene = new Scene(page);
            themeManager.applyTheme(formScene);
            dialogStage.setScene(formScene);
            CustomerFormController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setBill(bill);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}