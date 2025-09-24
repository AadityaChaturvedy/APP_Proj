package com.example.edutrack.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Subject {
    private long id;
    private long studentId;
    private final String subjectName;
    private final IntegerProperty attendance = new SimpleIntegerProperty();
    private final IntegerProperty marks = new SimpleIntegerProperty();

    public Subject(String subjectName) {
        this.subjectName = subjectName;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }
    public String getSubjectName() { return subjectName; }
    public int getAttendance() { return attendance.get(); }
    public void setAttendance(int attendance) { this.attendance.set(attendance); }
    public IntegerProperty attendanceProperty() { return attendance; }
    public int getMarks() { return marks.get(); }
    public void setMarks(int marks) { this.marks.set(marks); }
    public IntegerProperty marksProperty() { return marks; }
}