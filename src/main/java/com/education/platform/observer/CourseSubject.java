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
        for (IObserver observer : observers) {
            observer.update(message);
        }
    }
    public void publishAnnouncement(String announcement) {
        notifyObservers("New announcement: " + announcement);
    }

    public void submitAssignment(String studentName) {
        notifyObservers("Assignment submitted by: " + studentName);
    }
}
