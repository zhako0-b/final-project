package com.education.platform.observer;

public class TeacherNotifier implements IObserver {
    private final String teacherName;

    public TeacherNotifier(String teacherName) {
        this.teacherName = teacherName;
    }

    @Override
    public void update(String message) {
        System.out.println("[TEACHER] " + teacherName + ": " + message);
    }

    public String getTeacherName() {
        return teacherName;
    }
}
