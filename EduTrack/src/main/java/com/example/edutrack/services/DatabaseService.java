package com.example.edutrack.services;

import org.mindrot.jbcrypt.BCrypt;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:edutrack.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() throws SQLException {
        File dbFile = new File("edutrack.db");
        boolean isNewDB = !dbFile.exists();

        if (isNewDB) {
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                System.out.println("Creating new database and tables...");

                // Users Table with hashed password
                String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT NOT NULL UNIQUE," +
                        "password_hash TEXT NOT NULL," +
                        "role TEXT NOT NULL);";
                stmt.execute(createUserTable);

                // Students Table
                String createStudentTable = "CREATE TABLE IF NOT EXISTS students (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "age INTEGER NOT NULL," +
                        "address TEXT NOT NULL," +
                        "city TEXT NOT NULL," +
                        "state TEXT NOT NULL," +
                        "phone TEXT NOT NULL," +
                        "email TEXT NOT NULL UNIQUE);";
                stmt.execute(createStudentTable);

                // Subjects Table
                String createSubjectsTable = "CREATE TABLE IF NOT EXISTS subjects (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "student_id INTEGER NOT NULL," +
                        "subject_name TEXT NOT NULL," +
                        "attendance INTEGER NOT NULL," +
                        "marks INTEGER NOT NULL," +
                        "FOREIGN KEY(student_id) REFERENCES students(id) ON DELETE CASCADE);";
                stmt.execute(createSubjectsTable);

                // Insert Sample Data with HASHED password
                String adminPassword = "admin";
                String hashedPassword = BCrypt.hashpw(adminPassword, BCrypt.gensalt());
                stmt.execute("INSERT INTO users (username, password_hash, role) VALUES ('admin', '" + hashedPassword + "', 'ADMIN');");

                stmt.execute("INSERT INTO students (name, age, address, city, state, phone, email) VALUES " +
                        "('Alice Johnson', 22, '123 Maple St', 'Springfield', 'Illinois', '5551234567', 'alice@example.com');");
                stmt.execute("INSERT INTO students (name, age, address, city, state, phone, email) VALUES " +
                        "('Bob Smith', 21, '456 Oak Ave', 'Shelbyville', 'Illinois', '5557654321', 'bob@example.com');");

                String[] subjects = {"APP", "COA", "DSA", "Maths", "OS"};
                for (String subject : subjects) {
                    stmt.execute("INSERT INTO subjects (student_id, subject_name, attendance, marks) VALUES (1, '" + subject + "', " + (80 + (int) (Math.random() * 20)) + ", " + (75 + (int) (Math.random() * 25)) + ");");
                    stmt.execute("INSERT INTO subjects (student_id, subject_name, attendance, marks) VALUES (2, '" + subject + "', " + (85 + (int) (Math.random() * 15)) + ", " + (70 + (int) (Math.random() * 30)) + ");");
                }

                System.out.println("Sample data inserted.");
            }
        }
    }
}