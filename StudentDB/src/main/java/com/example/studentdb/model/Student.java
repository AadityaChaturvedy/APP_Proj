
package com.example.studentdb.model;

import java.time.LocalDate;

/**
 * Student entity.
 */
public class Student {
    private String studentName;
    private String rollNumber;
    private String course;
    private String batch;
    private LocalDate dob;
    private String mail;
    private String parentDetails;
    private double cgpa;

    public Student() {}

    public Student(String studentName, String rollNumber, String course, String batch, LocalDate dob, String mail, String parentDetails, double cgpa) {
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.course = course;
        this.batch = batch;
        this.dob = dob;
        this.mail = mail;
        this.parentDetails = parentDetails;
        this.cgpa = cgpa;
    }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }
    public String getParentDetails() { return parentDetails; }
    public void setParentDetails(String parentDetails) { this.parentDetails = parentDetails; }
    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }
}
