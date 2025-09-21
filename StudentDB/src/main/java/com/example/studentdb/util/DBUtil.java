
package com.example.studentdb.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database utility (SQLite).
 */
public class DBUtil {
    private static final String DB_URL = "jdbc:sqlite:students.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initDB() {
        String createTable = "CREATE TABLE IF NOT EXISTS students ("
                + "student_name TEXT NOT NULL,"
                + "roll_number TEXT PRIMARY KEY,"
                + "course TEXT,"
                + "batch TEXT,"
                + "dob TEXT,"
                + "mail TEXT,"
                + "parent_details TEXT,"
                + "cgpa REAL"
                + ");";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
