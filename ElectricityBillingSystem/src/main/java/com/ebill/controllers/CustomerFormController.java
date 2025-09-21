package com.ebill.controllers;

import com.ebill.models.Bill;
import com.ebill.services.DataService;
import com.ebill.services.LocalizationService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class CustomerFormController {

    @FXML private TextField customerNameField, addressField, cityField, stateField, phoneField, emailField, unitsConsumedField, billAmountField, meterNumberField;
    @FXML private ComboBox<String> billingMonthComboBox, paymentStatusComboBox;
    @FXML private Button saveButton, cancelButton;

    @FXML private Label nameLabel, meterLabel, addressLabel, cityLabel, stateLabel, phoneLabel, emailLabel, unitsLabel, monthLabel, statusLabel, amountLabel;

    private Stage dialogStage;
    private Bill bill;
    private boolean isNewBill;
    private final DataService dataService = DataService.getInstance();
    private final LocalizationService loc = LocalizationService.getInstance();
    private final Tooltip validationTooltip = new Tooltip();

    private final Node[] focusOrder;

    public CustomerFormController() {
        focusOrder = new Node[9];
    }

    @FXML
    public void initialize() {
        bindLocalizations();
        populateComboBoxes();
        addValidationListeners();
        setupKeyboardNavigation();

        focusOrder[0] = customerNameField;
        focusOrder[1] = addressField;
        focusOrder[2] = cityField;
        focusOrder[3] = stateField;
        focusOrder[4] = phoneField;
        focusOrder[5] = emailField;
        focusOrder[6] = unitsConsumedField;
        focusOrder[7] = billingMonthComboBox;
        focusOrder[8] = paymentStatusComboBox;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setBill(Bill bill) {
        if (bill == null) {
            this.bill = new Bill();
            isNewBill = true;
            meterNumberField.setText(loc.getString("form.auto_generated"));
        } else {
            this.bill = bill;
            isNewBill = false;
            prefillForm();
        }
        bindFieldsToModel();
    }

    private void prefillForm() {
        customerNameField.setText(bill.getCustomerName());
        meterNumberField.setText(String.valueOf(bill.getMeterNumber()));
        addressField.setText(bill.getAddress());
        cityField.setText(bill.getCity());
        stateField.setText(bill.getState());
        phoneField.setText(bill.getPhone());
        emailField.setText(bill.getEmail());
        unitsConsumedField.setText(String.valueOf(bill.getUnitsConsumed()));
        billingMonthComboBox.setValue(bill.getBillingMonth());
        paymentStatusComboBox.setValue(bill.getPaymentStatus());
    }

    private void bindFieldsToModel() {
        customerNameField.textProperty().addListener((obs, old, val) -> bill.setCustomerName(val));
        addressField.textProperty().addListener((obs, old, val) -> bill.setAddress(val));
        cityField.textProperty().addListener((obs, old, val) -> bill.setCity(val));
        stateField.textProperty().addListener((obs, old, val) -> bill.setState(val));
        phoneField.textProperty().addListener((obs, old, val) -> bill.setPhone(val));
        emailField.textProperty().addListener((obs, old, val) -> bill.setEmail(val));
        unitsConsumedField.textProperty().addListener((obs, old, val) -> {
            try {
                bill.setUnitsConsumed(Integer.parseInt(val));
            } catch (NumberFormatException e) {
                bill.setUnitsConsumed(0);
            }
        });
        billingMonthComboBox.valueProperty().addListener((obs, old, val) -> bill.setBillingMonth(val));
        paymentStatusComboBox.valueProperty().addListener((obs, old, val) -> bill.setPaymentStatus(val));

        billAmountField.textProperty().bind(bill.billAmountProperty().asString("₹ %.2f"));
    }

    private void populateComboBoxes() {
        billingMonthComboBox.setItems(FXCollections.observableArrayList(
                Arrays.stream(Month.values())
                        .map(m -> m.getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                        .collect(Collectors.toList())
        ));
        paymentStatusComboBox.setItems(FXCollections.observableArrayList("Paid", "Unpaid"));
    }

    private void addValidationListeners() {
        addValidation(customerNameField, "^[a-zA-Z\\s]+$", "tips.customer_name");
        addValidation(phoneField, "^\\d{10}$", "tips.phone");
        addValidation(emailField, "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", "tips.email");
        addValidation(unitsConsumedField, "^[1-9]\\d*$", "tips.units");
    }

    private void addValidation(TextField field, String regex, String tooltipKey) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            validateField(field, regex, tooltipKey);
            updateSaveButtonState();
        });
    }

    private boolean validateField(TextField field, String regex, String tooltipKey) {
        boolean isValid = field.getText().matches(regex);
        if (isValid) {
            field.getStyleClass().remove("invalid-field");
            Tooltip.uninstall(field, validationTooltip);
        } else {
            if (!field.getStyleClass().contains("invalid-field")) {
                field.getStyleClass().add("invalid-field");
            }
            validationTooltip.textProperty().bind(loc.getProperty(tooltipKey));
            Tooltip.install(field, validationTooltip);
        }
        return isValid;
    }

    private boolean isFormValid() {
        return validateField(customerNameField, "^[a-zA-Z\\s]+$", "tips.customer_name") &&
               validateField(phoneField, "^\\d{10}$", "tips.phone") &&
               validateField(emailField, "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", "tips.email") &&
               validateField(unitsConsumedField, "^[1-9]\\d*$", "tips.units") &&
               billingMonthComboBox.getValue() != null && !billingMonthComboBox.getValue().isEmpty() &&
               paymentStatusComboBox.getValue() != null && !paymentStatusComboBox.getValue().isEmpty();
    }

    private void updateSaveButtonState() {
        saveButton.setDisable(!isFormValid());
    }

    private void setupKeyboardNavigation() {
        Platform.runLater(() -> {
            dialogStage.getScene().setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    focusNext();
                    event.consume();
                } else if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN).match(event)) {
                    if (!saveButton.isDisabled()) {
                        handleSave();
                    }
                    event.consume();
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    handleCancel();
                    event.consume();
                }
            });
        });
    }

    private void focusNext() {
        Node focusedNode = dialogStage.getScene().getFocusOwner();
        for (int i = 0; i < focusOrder.length; i++) {
            if (focusOrder[i] == focusedNode) {
                int nextIndex = (i + 1) % focusOrder.length;
                focusOrder[nextIndex].requestFocus();
                break;
            }
        }
    }

    @FXML
    private void handleSave() {
        if (isFormValid()) {
            if (isNewBill) {
                dataService.addBill(bill);
            } else {
                dataService.updateBill(bill);
            }
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void bindLocalizations() {
        nameLabel.textProperty().bind(loc.getProperty("form.customer_name"));
        meterLabel.textProperty().bind(loc.getProperty("form.meter_number"));
        addressLabel.textProperty().bind(loc.getProperty("form.address"));
        cityLabel.textProperty().bind(loc.getProperty("form.city"));
        stateLabel.textProperty().bind(loc.getProperty("form.state"));
        phoneLabel.textProperty().bind(loc.getProperty("form.phone"));
        emailLabel.textProperty().bind(loc.getProperty("form.email"));
        unitsLabel.textProperty().bind(loc.getProperty("form.units_consumed"));
        monthLabel.textProperty().bind(loc.getProperty("form.billing_month"));
        statusLabel.textProperty().bind(loc.getProperty("form.payment_status"));
        amountLabel.textProperty().bind(loc.getProperty("form.bill_amount"));

        saveButton.textProperty().bind(loc.getProperty("ui.save_button"));
        cancelButton.textProperty().bind(loc.getProperty("ui.cancel_button"));
    }
}