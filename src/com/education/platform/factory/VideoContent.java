package com.education.platform.factory;

public class VideoContent implements EducationalContent {
    private final String title;
    private final String url;

    public VideoContent(String title, String url) {
        this.title = title;
        this.url = url;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String getType() {
        return "VIDEO";
    }

    @Override
    public String render() {
        return getType() + ": " + title;
    }

    // TODO: add validation and a detailed content preview.
}
