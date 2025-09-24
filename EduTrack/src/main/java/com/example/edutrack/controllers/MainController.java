package com.example.edutrack.controllers;

import com.example.edutrack.models.Student;
import com.example.edutrack.models.Subject;
import com.example.edutrack.services.StudentService;
import com.example.edutrack.utils.AlertUtils;
import com.example.edutrack.utils.ThemeManager;
import com.example.edutrack.utils.ToastUtil;
import com.example.edutrack.utils.Validator;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class MainController {

    // FXML Components
    @FXML private TableView<Student> studentTableView;
    @FXML private TableColumn<Student, Long> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TextField nameField, ageField, addressField, cityField, stateField, phoneField, emailField;
    @FXML private TextField appAttField, appMarksField, coaAttField, coaMarksField, dsaAttField, dsaMarksField, mathsAttField, mathsMarksField, osAttField, osMarksField;
    @FXML private Button saveButton, deleteButton;
    @FXML private ToggleButton themeToggleButton;
    @FXML private SplitPane mainSplitPane;
    @FXML private TitledPane editorPane;
    @FXML private TextField searchField;

    // Services and Data
    private final StudentService studentService = new StudentService();
    private Student currentStudent;
    private FilteredList<Student> filteredStudents;
    private final Map<TextField, Predicate<String>> validationMap = new HashMap<>();
    private final Map<String, TextField[]> subjectFieldsMap = new HashMap<>();

    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");

    @FXML
    public void initialize() {
        setupTable();
        loadStudentData();
        setupFormBindingsAndValidation();
        setupEventListeners();
        setupSearchFilter();

        Platform.runLater(() -> {
            setupTheme();
            setupKeyboardShortcuts();
            mainSplitPane.setDividerPositions(0.35);
        });

        clearForm();
        editorPane.setDisable(true);
    }

    private void setupTheme() {
        ThemeManager.Theme currentTheme = ThemeManager.loadThemePreference();
        themeToggleButton.setSelected(currentTheme == ThemeManager.Theme.DARK);
        ThemeManager.applyTheme(themeToggleButton.getScene(), currentTheme);
        themeToggleButton.setText(currentTheme == ThemeManager.Theme.DARK ? "☀️" : "🌙");
        
        themeToggleButton.setOnAction(event -> {
            ThemeManager.Theme newTheme = themeToggleButton.isSelected() ? ThemeManager.Theme.DARK : ThemeManager.Theme.LIGHT;
            ThemeManager.applyTheme(themeToggleButton.getScene(), newTheme);
            themeToggleButton.setText(newTheme == ThemeManager.Theme.DARK ? "☀️" : "🌙");
        });
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    private void loadStudentData() {
        ObservableList<Student> students = studentService.getAllStudents();
        filteredStudents = new FilteredList<>(students, p -> true);
        studentTableView.setItems(filteredStudents);
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredStudents.setPredicate(student -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newVal.toLowerCase();
                if (student.getName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (student.getEmail().toLowerCase().contains(lowerCaseFilter)) return true;
                if (student.getCity().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });
    }

    private void setupFormBindingsAndValidation() {
        subjectFieldsMap.put("APP", new TextField[]{appAttField, appMarksField});
        subjectFieldsMap.put("COA", new TextField[]{coaAttField, coaMarksField});
        subjectFieldsMap.put("DSA", new TextField[]{dsaAttField, dsaMarksField});
        subjectFieldsMap.put("Maths", new TextField[]{mathsAttField, mathsMarksField});
        subjectFieldsMap.put("OS", new TextField[]{osAttField, osMarksField});

        addValidationRule(nameField, Validator::isNameValid, "Name must contain only letters and spaces.");
        addValidationRule(ageField, Validator::isAgeValid, "Age must be a number between 5 and 120.");
        addValidationRule(addressField, s -> !Validator.isFieldEmpty(s), "Address is required.");
        addValidationRule(cityField, Validator::isCityOrStateValid, "City must contain only letters and spaces.");
        addValidationRule(stateField, Validator::isCityOrStateValid, "State must contain only letters and spaces.");
        addValidationRule(phoneField, Validator::isPhoneValid, "Phone must be exactly 10 digits.");
        addValidationRule(emailField, Validator::isEmailValid, "Enter a valid email address.");

        subjectFieldsMap.values().stream().flatMap(Arrays::stream).forEach(field ->
            addValidationRule(field, Validator::isAcademicValueValid, "Must be a number between 0 and 100.")
        );
    }

    private void addValidationRule(TextField field, Predicate<String> validationLogic, String tooltipMessage) {
        field.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (wasFocused && !isNowFocused) {
                validateField(field, validationLogic, tooltipMessage);
            }
        });
        field.setTooltip(new Tooltip(tooltipMessage));
        validationMap.put(field, validationLogic);
    }

    private void validateField(TextField field, Predicate<String> validationLogic, String tooltipMessage) {
        boolean isValid = validationLogic.test(field.getText());
        field.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, !isValid);
        field.getTooltip().setText(isValid ? "Valid" : tooltipMessage);
    }

    private boolean isFormValid() {
        return validationMap.entrySet().stream()
                .allMatch(entry -> entry.getValue().test(entry.getKey().getText()));
    }

    private void setupEventListeners() {
        studentTableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateForm(newSelection);
                }
            });
    }

    private void populateForm(Student student) {
        currentStudent = student;
        editorPane.setDisable(false);
        editorPane.setText("Editing Student: " + student.getName());

        nameField.setText(student.getName());
        ageField.setText(String.valueOf(student.getAge()));
        addressField.setText(student.getAddress());
        cityField.setText(student.getCity());
        stateField.setText(student.getState());
        phoneField.setText(student.getPhone());
        emailField.setText(student.getEmail());

        subjectFieldsMap.forEach((subjectName, fields) -> {
            Subject subject = student.getSubjects().get(subjectName);
            if (subject != null) {
                fields[0].setText(String.valueOf(subject.getAttendance()));
                fields[1].setText(String.valueOf(subject.getMarks()));
            } else {
                fields[0].clear();
                fields[1].clear();
            }
        });

        validationMap.keySet().forEach(field -> field.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false));
    }

    private void clearForm() {
        currentStudent = null;
        editorPane.setDisable(true);
        editorPane.setText("Student Details");

        studentTableView.getSelectionModel().clearSelection();

        validationMap.keySet().forEach(field -> {
            field.clear();
            field.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false);
        });
    }

    @FXML
    private void handleAdd() {
        clearForm();
        currentStudent = new Student();
        editorPane.setDisable(false);
        editorPane.setText("Add New Student");
        nameField.requestFocus();
    }

    @FXML
    private void handleSave() {
        if (!isFormValid()) {
            ToastUtil.showError("Please fix the errors before saving.");
            return;
        }

        if (currentStudent == null) {
            ToastUtil.showError("No student selected or being created.");
            return;
        }

        Optional<ButtonType> result = AlertUtils.showConfirmation("Confirm Save", "Are you sure you want to save these changes?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updateStudentFromForm();
            try {
                studentService.saveStudent(currentStudent);
                ToastUtil.showSuccess("Student saved successfully!");
                loadStudentData();
                clearForm();
            } catch (SQLException e) {
                AlertUtils.showError("Database Error: Could not save student. Email may already exist.", null);
            }
        }
    }

    private void updateStudentFromForm() {
        currentStudent.setName(nameField.getText());
        currentStudent.setAge(Integer.parseInt(ageField.getText()));
        currentStudent.setAddress(addressField.getText());
        currentStudent.setCity(cityField.getText());
        currentStudent.setState(stateField.getText());
        currentStudent.setPhone(phoneField.getText());
        currentStudent.setEmail(emailField.getText());

        subjectFieldsMap.forEach((subjectName, fields) -> {
            Subject subject = currentStudent.getSubjects().computeIfAbsent(subjectName, Subject::new);
            subject.setAttendance(Integer.parseInt(fields[0].getText()));
            subject.setMarks(Integer.parseInt(fields[1].getText()));
        });
    }

    @FXML
    private void handleDelete() {
        Student selectedStudent = studentTableView.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            AlertUtils.showError("No Selection", "Please select a student to delete.");
            return;
        }

        Optional<ButtonType> result = AlertUtils.showConfirmation(
                "Confirm Deletion",
                "Are you sure you want to delete " + selectedStudent.getName() + "?"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                studentService.deleteStudent(selectedStudent);
                ToastUtil.showSuccess("Student deleted successfully.");
                loadStudentData();
                clearForm();
            } catch (SQLException e) {
                AlertUtils.showError("Database Error", "Could not delete student.");
            }
        }
    }

    private void setupKeyboardShortcuts() {
        Scene scene = nameField.getScene();
        if (scene == null) return;

        scene.getAccelerators().put(
            new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN),
            this::handleSave
        );

        scene.getAccelerators().put(
            new KeyCodeCombination(KeyCode.ESCAPE),
            this::handleCancel
        );
    }

    @FXML
    private void handleCancel() {
        if (!editorPane.isDisable()) {
             Optional<ButtonType> result = AlertUtils.showConfirmation("Confirm Cancel", "Are you sure you want to discard changes?");
             if (result.isPresent() && result.get() == ButtonType.OK) {
                clearForm();
             }
        }
    }
}