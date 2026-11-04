package com.education.platform.model;

public class Assignment {
    private final String title;
    private final String description;

    public Assignment(String title, String description) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Assignment title is required");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description is required");
        this.title = title;
        this.description = description;
    }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
}
