
package com.education.platform.observer;

import java.util.List;

public class TeacherNotifier implements IObserver {

    private final String teacherName;
    private final List<String> inbox;

    public TeacherNotifier(String teacherName) {
        this(teacherName, null);
    }

    public TeacherNotifier(String teacherName, List<String> inbox) {
        this.teacherName = teacherName;
        this.inbox = inbox;
    }

    @Override
    public void update(String message) {

        System.out.println(
                "[TEACHER] " + teacherName + ": " + message
        );

        if (inbox != null) {
            inbox.add(teacherName + "|" + message);
        }
    }

    public String getTeacherName() {
        return teacherName;
    }
}
