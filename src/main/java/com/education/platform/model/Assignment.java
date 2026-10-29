package com.education.platform.model;

public class Assignment {
    private final String title;
    private final String description;

    public Assignment(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
