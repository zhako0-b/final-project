package com.education.platform.factory;

public class TextContent implements EducationalContent {
    private final String title;
    private final String text;

    public TextContent(String title, String text) {
        this.title = title;
        this.text = text;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getType() {
        return "TEXT";
    }

    @Override
    public String render() {
        return getType() + ": " + title;
    }

    // TODO: add validation and a detailed content preview.
}
