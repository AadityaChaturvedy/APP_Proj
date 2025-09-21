
package com.example.studentdb.controller;

import com.example.studentdb.dao.StudentDAO;
import com.example.studentdb.model.Student;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * Controller for Add/Edit form with validation.
 */
public class AddEditStudentController {
    @FXML private TextField nameField, rollField, courseField, batchField, mailField, cgpaField;
    @FXML private DatePicker dobPicker;
    @FXML private TextArea parentField;

    private Stage dialogStage;
    private Student current;

    public void setDialogStage(Stage stage) { this.dialogStage = stage; }

    public void setStudent(Student s) {
        this.current = s;
        nameField.setText(s.getStudentName());
        rollField.setText(s.getRollNumber());
        rollField.setDisable(true);
        courseField.setText(s.getCourse());
        batchField.setText(s.getBatch());
        dobPicker.setValue(s.getDob());
        mailField.setText(s.getMail());
        parentField.setText(s.getParentDetails());
        cgpaField.setText(String.valueOf(s.getCgpa()));
    }

    @FXML private void onSave() {
        if (!validate()) return;
        Student s = new Student();
        s.setStudentName(nameField.getText().trim());
        s.setRollNumber(rollField.getText().trim());
        s.setCourse(courseField.getText().trim());
        s.setBatch(batchField.getText().trim());
        s.setDob(dobPicker.getValue());
        s.setMail(mailField.getText().trim());
        s.setParentDetails(parentField.getText().trim());
        s.setCgpa(Double.parseDouble(cgpaField.getText().trim()));

        boolean ok;
        if (current == null) ok = StudentDAO.addStudent(s); else ok = StudentDAO.updateStudent(s);

        if (ok) dialogStage.close(); else {
            Alert a = new Alert(Alert.AlertType.ERROR, "Database operation failed.");
            a.showAndWait();
        }
    }

    @FXML private void onCancel() { dialogStage.close(); }

    private boolean validate() {
        StringBuilder err = new StringBuilder();
        if (nameField.getText().trim().isEmpty()) err.append("Name is required\n");
        if (rollField.getText().trim().isEmpty()) err.append("Roll number is required\n");
        if (dobPicker.getValue() == null) err.append("DOB is required\n");
        if (!mailField.getText().trim().matches("^\\S+@\\S+\\.\\S+$")) err.append("Valid email required\n");
        try { double cg = Double.parseDouble(cgpaField.getText().trim()); if (cg < 0 || cg > 10) err.append("CGPA must be between 0 and 10\n"); }
        catch (NumberFormatException ex) { err.append("CGPA must be a number\n"); }

        if (err.length() > 0) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setHeaderText("Validation failed");
            a.setContentText(err.toString());
            a.showAndWait();
            return false;
        }
        return true;
    }
}
