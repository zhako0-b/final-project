package com.education.platform.observer;
import java.util.*;

public class CourseSubject implements ISubject {
    private final Map<IObserver, Set<EventType>> observers =
            new LinkedHashMap<>();

    @Override
    public void addObserver(IObserver observer) {
        if (observer != null) {
            observers.putIfAbsent(
                    observer, EnumSet.allOf(EventType.class)
            );
        }
    }

    public void subscribe(IObserver observer, EventType type) {
        Objects.requireNonNull(observer);
        Objects.requireNonNull(type);

        observers.computeIfAbsent(
                observer, key -> EnumSet.noneOf(EventType.class)
        ).add(type);
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        publishEvent(new CourseEvent(EventType.ANNOUNCEMENT, message));
    }

    public void publishEvent(CourseEvent event) {
        Objects.requireNonNull(event);
        for (Map.Entry<IObserver, Set<EventType>> entry :
                new ArrayList<>(observers.entrySet())) {

            if (!entry.getValue().contains(event.getType())) {
                continue;
            }
            IObserver observer = entry.getKey();

            if (event.getType() == EventType.ASSIGNMENT_GRADED
                    && observer instanceof StudentNotifier) {
                continue;
            }
            observer.update(event.getMessage());
        }
    }

    public void publishAnnouncement(String announcement) {
        publishEvent(new CourseEvent(
                EventType.ANNOUNCEMENT,
                "New announcement: " + announcement
        ));
    }

    public void submitAssignment(String studentName) {
        publishEvent(new CourseEvent(
                EventType.ASSIGNMENT_SUBMITTED,
                "Assignment submitted by: " + studentName
        ));
    }

    public void gradeAssignment(String studentName, int grade) {
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100");
        }
        publishEvent(new CourseEvent(
                EventType.ASSIGNMENT_GRADED,
                studentName + " received grade: " + grade
        ));

        for (Map.Entry<IObserver, Set<EventType>> entry :
                new ArrayList<>(observers.entrySet())) {
            IObserver observer = entry.getKey();

            if (observer instanceof StudentNotifier
                    && entry.getValue().contains(EventType.ASSIGNMENT_GRADED)
                    && ((StudentNotifier) observer)
                    .getStudentName().equals(studentName)) {

                observer.update("You received grade: " + grade);
            }
        }
    }
}
