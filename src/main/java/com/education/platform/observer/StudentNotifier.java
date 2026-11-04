package com.education.platform.observer;

public class StudentNotifier implements IObserver {
    private final String studentName;
    public StudentNotifier(String studentName) {
        this.studentName = studentName;
    }

    @Override
    public void update(String message) {
        System.out.println("[STUDENT] " + studentName + ": " + message);
    }
    public String getStudentName() {
        return studentName;
    }
}
