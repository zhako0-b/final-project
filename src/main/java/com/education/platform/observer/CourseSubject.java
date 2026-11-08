package com.education.platform.observer;
import java.util.*;

public class CourseSubject implements ISubject {
    private final String courseTitle;
    private final Map<IObserver, Set<EventType>> observers = new LinkedHashMap<>();

    public CourseSubject() {
        this("");
    }

    public CourseSubject(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void attach(IObserver observer) {
        addObserver(observer);
    }

    public void detach(IObserver observer) {
        removeObserver(observer);
    }

    @Override
    public void addObserver(IObserver observer) {
        if (observer != null) {
            observers.putIfAbsent(
                    observer,
                    EnumSet.allOf(EventType.class)
            );
        }
    }

    public void subscribe(IObserver observer, EventType type) {
        Objects.requireNonNull(observer);
        Objects.requireNonNull(type);

        observers.computeIfAbsent(
                observer,
                key -> EnumSet.noneOf(EventType.class)
        ).add(type);
    }

    public void unsubscribe(IObserver observer, EventType type) {
        Set<EventType> types = observers.get(observer);

        if (types != null) {
            types.remove(type);
        }
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        publishEvent(new CourseEvent(
                EventType.ANNOUNCEMENT,
                message
        ));
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
            observer.update(prefix() + event.getMessage());
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
            throw new IllegalArgumentException(
                    "Grade must be between 0 and 100"
            );
        }

        publishEvent(new CourseEvent(
                EventType.ASSIGNMENT_GRADED,
                studentName + " received grade: " + grade
        ));

        for (Map.Entry<IObserver, Set<EventType>> entry :
                new ArrayList<>(observers.entrySet())) {
            IObserver observer = entry.getKey();

            if (observer instanceof StudentNotifier student
                    && entry.getValue().contains(EventType.ASSIGNMENT_GRADED)
                    && student.getStudentName().equals(studentName)) {
                observer.update(
                        prefix() + "You received grade: " + grade
                );
            }
        }
    }

    private String prefix() {
        return courseTitle.isBlank()
                ? ""
                : "[" + courseTitle + "] ";
    }
}
