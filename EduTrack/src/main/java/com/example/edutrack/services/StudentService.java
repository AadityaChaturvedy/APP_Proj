package com.example.edutrack.services;

import com.example.edutrack.models.Student;
import com.example.edutrack.models.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.Map;

public class StudentService {

    private static final String[] SUBJECT_NAMES = {"APP", "COA", "DSA", "Maths", "OS"};

    public ObservableList<Student> getAllStudents() {
        ObservableList<Student> students = FXCollections.observableArrayList();
        String studentSql = "SELECT * FROM students";
        String subjectSql = "SELECT * FROM subjects WHERE student_id = ?";

        try (Connection conn = DatabaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(studentSql)) {

            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getLong("id"));
                student.setName(rs.getString("name"));
                student.setAge(rs.getInt("age"));
                student.setAddress(rs.getString("address"));
                student.setCity(rs.getString("city"));
                student.setState(rs.getString("state"));
                student.setPhone(rs.getString("phone"));
                student.setEmail(rs.getString("email"));

                try (PreparedStatement pstmt = conn.prepareStatement(subjectSql)) {
                    pstmt.setLong(1, student.getId());
                    ResultSet subjectRs = pstmt.executeQuery();
                    while (subjectRs.next()) {
                        String subjectName = subjectRs.getString("subject_name");
                        Subject subject = new Subject(subjectName);
                        subject.setId(subjectRs.getLong("id"));
                        subject.setStudentId(student.getId());
                        subject.setAttendance(subjectRs.getInt("attendance"));
                        subject.setMarks(subjectRs.getInt("marks"));
                        student.getSubjects().put(subjectName, subject);
                    }
                }
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
    
    public void saveStudent(Student student) throws SQLException {
        if (student.getId() == 0) {
            addStudent(student);
        } else {
            updateStudent(student);
        }
    }

    private void addStudent(Student student) throws SQLException {
        String studentSql = "INSERT INTO students(name, age, address, city, state, phone, email) VALUES(?,?,?,?,?,?,?)";
        String subjectSql = "INSERT INTO subjects(student_id, subject_name, attendance, marks) VALUES(?,?,?,?)";
        
        Connection conn = null;
        try {
            conn = DatabaseService.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(studentSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, student.getName());
                pstmt.setInt(2, student.getAge());
                pstmt.setString(3, student.getAddress());
                pstmt.setString(4, student.getCity());
                pstmt.setString(5, student.getState());
                pstmt.setString(6, student.getPhone());
                pstmt.setString(7, student.getEmail());
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        student.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating student failed, no ID obtained.");
                    }
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(subjectSql)) {
                 for (String subjectName : SUBJECT_NAMES) {
                    Subject subject = student.getSubjects().getOrDefault(subjectName, new Subject(subjectName));
                    pstmt.setLong(1, student.getId());
                    pstmt.setString(2, subjectName);
                    pstmt.setInt(3, subject.getAttendance());
                    pstmt.setInt(4, subject.getMarks());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }


    private void updateStudent(Student student) throws SQLException {
        String studentSql = "UPDATE students SET name=?, age=?, address=?, city=?, state=?, phone=?, email=? WHERE id=?";
        String subjectSql = "UPDATE subjects SET attendance=?, marks=? WHERE student_id=? AND subject_name=?";

        Connection conn = null;
        try {
            conn = DatabaseService.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(studentSql)) {
                pstmt.setString(1, student.getName());
                pstmt.setInt(2, student.getAge());
                pstmt.setString(3, student.getAddress());
                pstmt.setString(4, student.getCity());
                pstmt.setString(5, student.getState());
                pstmt.setString(6, student.getPhone());
                pstmt.setString(7, student.getEmail());
                pstmt.setLong(8, student.getId());
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(subjectSql)) {
                for (Map.Entry<String, Subject> entry : student.getSubjects().entrySet()) {
                    Subject subject = entry.getValue();
                    pstmt.setInt(1, subject.getAttendance());
                    pstmt.setInt(2, subject.getMarks());
                    pstmt.setLong(3, student.getId());
                    pstmt.setString(4, entry.getKey());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public void deleteStudent(Student student) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, student.getId());
            pstmt.executeUpdate();
        }
    }
}