package com.education.platform.observer;

public final class CourseEvent {
    private final EventType type;
    private final String message;

    public CourseEvent(EventType type, String message) {
        if (type == null || message == null) {
            throw new IllegalArgumentException("Event data cannot be null");
        }
        this.type = type;
        this.message = message;
    }

    public EventType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
