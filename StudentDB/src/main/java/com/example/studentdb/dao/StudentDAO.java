
package com.example.studentdb.dao;

import com.example.studentdb.model.Student;
import com.example.studentdb.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDAO with CRUD.
 */
public class StudentDAO {

    public static boolean addStudent(Student s) {
        String sql = "INSERT INTO students(student_name, roll_number, course, batch, dob, mail, parent_details, cgpa) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStudentName());
            ps.setString(2, s.getRollNumber());
            ps.setString(3, s.getCourse());
            ps.setString(4, s.getBatch());
            ps.setString(5, s.getDob().toString());
            ps.setString(6, s.getMail());
            ps.setString(7, s.getParentDetails());
            ps.setDouble(8, s.getCgpa());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DBUtil.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Student getStudentByRoll(String roll) {
        String sql = "SELECT * FROM students WHERE roll_number = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roll);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToStudent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateStudent(Student s) {
        String sql = "UPDATE students SET student_name=?, course=?, batch=?, dob=?, mail=?, parent_details=?, cgpa=? WHERE roll_number=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStudentName());
            ps.setString(2, s.getCourse());
            ps.setString(3, s.getBatch());
            ps.setString(4, s.getDob().toString());
            ps.setString(5, s.getMail());
            ps.setString(6, s.getParentDetails());
            ps.setDouble(7, s.getCgpa());
            ps.setString(8, s.getRollNumber());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteStudent(String roll) {
        String sql = "DELETE FROM students WHERE roll_number = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roll);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        String name = rs.getString("student_name");
        String roll = rs.getString("roll_number");
        String course = rs.getString("course");
        String batch = rs.getString("batch");
        LocalDate dob = LocalDate.parse(rs.getString("dob"));
        String mail = rs.getString("mail");
        String parent = rs.getString("parent_details");
        double cgpa = rs.getDouble("cgpa");
        return new Student(name, roll, course, batch, dob, mail, parent, cgpa);
    }
}
