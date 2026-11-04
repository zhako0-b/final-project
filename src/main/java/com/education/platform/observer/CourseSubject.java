package com.education.platform.observer;

import java.util.ArrayList;
import java.util.List;

public class CourseSubject implements ISubject {
    private final List<IObserver> observers = new ArrayList<>();

    @Override
    public void addObserver(IObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (IObserver observer : new ArrayList<>(observers)) {
            observer.update(message);
        }
    }
    public void publishAnnouncement(String announcement) {
        notifyObservers("New announcement: " + announcement);
    }

    public void submitAssignment(String studentName) {
        notifyObservers("Assignment submitted by: " + studentName);
    }

    public void gradeAssignment(String studentName, int grade) {
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100");
        }
        notifyObservers(studentName + " received grade: " + grade);
    }
}
