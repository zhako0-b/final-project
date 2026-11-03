package com.education.platform.observer;

public class StudentNotifier implements IObserver {
    private String studentName;
    public StudentNotifier(String studentName) {
        this.studentName = studentName;
    }

    @Override
    public void update(String message) {
        System.out.println("Student " + studentName + " received: " + message);
    }
}
