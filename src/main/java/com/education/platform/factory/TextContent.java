package com.education.platform.factory;

public class TextContent implements EducationalContent {
    private final String title;
    private final String text;

    public TextContent(String title, String text) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Content title is required");
        if (text == null || text.isBlank()) throw new IllegalArgumentException("Text is required");
        this.title = title;
        this.text = text;
    }
    @Override
    public String getTitle() { return title; }
    public String getText() { return text; }
    @Override
    public String getType() { return "TEXT"; }
    @Override
    public String render() { return getType() + ": " + title + " | " + text; }
}
