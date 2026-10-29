package com.education.platform.factory;

public class VideoContentFactory extends ContentFactory {
    @Override
    public EducationalContent createContent(String title, String data) {
        return new VideoContent(title, data);
    }
}
