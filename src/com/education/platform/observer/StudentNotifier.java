package com.education.platform.observer;

import java.util.List;

public class StudentNotifier implements IObserver {

    private final String studentName;
    private final List<String> inbox;

    public StudentNotifier(String studentName) {
        this(studentName, null);
    }

    public StudentNotifier(String studentName, List<String> inbox) {
        this.studentName = studentName;
        this.inbox = inbox;
    }

    @Override
    public void update(String message) {

        System.out.println(
                "[STUDENT] " + studentName + ": " + message
        );

        if (inbox != null) {
            inbox.add(studentName + "|" + message);
        }
    }

    public String getStudentName() {
        return studentName;
    }
}
