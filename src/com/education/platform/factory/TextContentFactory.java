package com.education.platform.factory;

public class TextContentFactory extends ContentFactory {
    @Override
    public EducationalContent createContent(String title, String data) {
        return new TextContent(title, data);
    }
}
