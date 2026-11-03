package com.education.platform.observer;

public class TeacherNotifier implements IObserver {
    private String teacherName;
    public TeacherNotifier(String teacherName) {
        this.teacherName = teacherName;
    }

    @Override
    public void update(String message) {
        System.out.println("Teacher " + teacherName + " received: " + message);
    }
}