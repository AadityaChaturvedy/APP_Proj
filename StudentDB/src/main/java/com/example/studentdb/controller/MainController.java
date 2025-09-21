
package com.example.studentdb.controller;

import com.example.studentdb.dao.StudentDAO;
import com.example.studentdb.model.Student;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Main controller with polished UI interactions.
 */
public class MainController {
    @FXML private TableView<Student> table;
    @FXML private TableColumn<Student, String> colName, colRoll, colCourse, colBatch, colDob, colMail;
    @FXML private TableColumn<Student, Double> colCgpa;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;

    private final ObservableList<Student> students = FXCollections.observableArrayList();
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public void initialize() {
        // Cell value factories
        colName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colRoll.setCellValueFactory(new PropertyValueFactory<>("rollNumber"));
        colCourse.setCellValueFactory(new PropertyValueFactory<>("course"));
        colBatch.setCellValueFactory(new PropertyValueFactory<>("batch"));
        colDob.setCellValueFactory(cell -> javafx.beans.property.SimpleStringProperty.stringExpression(
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    if (cell.getValue().getDob() == null) return "-";
                    return cell.getValue().getDob().format(df);
                })
        ));
        colMail.setCellValueFactory(new PropertyValueFactory<>("mail"));
        colCgpa.setCellValueFactory(new PropertyValueFactory<>("cgpa"));

        // Custom formatting for CGPA column
        colCgpa.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                    setStyle(""); // reset
                } else {
                    setText(String.format("%.2f", value));
                    if (value < 4.0) setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;"); // low cgpa redish
                    else if (value >= 8.0) setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;"); // high cgpa greenish
                    else setStyle(""); // normal
                }
            }
        });

        // Row factory for hover and double-click edit
        table.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>() {
                @Override
                protected void updateItem(Student item, boolean empty) {
                    super.updateItem(item, empty);
                    pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("dim"), empty || item == null);
                }
            };

            // Context menu per row
            MenuItem edit = new MenuItem("Edit");
            edit.setOnAction(e -> openAddEdit(row.getItem()));
            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction(e -> deleteRow(row.getItem()));
            ContextMenu menu = new ContextMenu(edit, delete);
            row.contextMenuProperty().bind(javafx.beans.binding.Bindings.when(row.emptyProperty()).then((ContextMenu)null).otherwise(menu));

            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    openAddEdit(row.getItem());
                }
            });
            return row;
        });

        // Add fade-in animation when loading table
        FadeTransition ft = new FadeTransition(Duration.millis(400), table);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        loadAll();
    }

    private void loadAll() {
        List<Student> list = StudentDAO.getAllStudents();
        students.setAll(list);
        table.setItems(students);
        statusLabel.setText("Total: " + students.size());
    }

    @FXML
    private void onSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) { loadAll(); return; }
        // try roll first
        Student s = StudentDAO.getStudentByRoll(q);
        if (s != null) {
            students.setAll(s);
            table.setItems(students);
            statusLabel.setText("Found: " + q);
            return;
        }
        // otherwise filter by name (simple client-side filter)
        ObservableList<Student> filtered = students.filtered(stu ->
                stu.getStudentName().toLowerCase().contains(q.toLowerCase()) ||
                stu.getRollNumber().toLowerCase().contains(q.toLowerCase()));
        if (filtered.isEmpty()) {
            showNotFound(q);
        } else {
            table.setItems(filtered);
            statusLabel.setText("Filtered: " + filtered.size());
        }
    }

    private void showNotFound(String q) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("No results");
        a.setContentText("No student found for: " + q);
        a.showAndWait();
    }

    @FXML private void onAdd() { openAddEdit(null); }
    @FXML private void onEdit() {
        Student sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { statusLabel.setText("Select a student first"); return; }
        openAddEdit(sel);
    }
    @FXML private void onDelete() {
        Student sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { statusLabel.setText("Select a student first"); return; }
        deleteRow(sel);
    }

    private void deleteRow(Student sel) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete student " + sel.getRollNumber() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                boolean ok = StudentDAO.deleteStudent(sel.getRollNumber());
                if (ok) loadAll(); else statusLabel.setText("Delete failed");
            }
        });
    }

    private void openAddEdit(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_edit.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(student == null ? "Add Student" : "Edit Student");
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            dialog.setScene(scene);

            AddEditStudentController ctrl = loader.getController();
            ctrl.setDialogStage(dialog);
            if (student != null) ctrl.setStudent(student);

            dialog.showAndWait();
            loadAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
