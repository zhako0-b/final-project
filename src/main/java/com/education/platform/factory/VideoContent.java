package com.education.platform.factory;

public class VideoContent implements EducationalContent {
    private final String title;
    private final String url;

    public VideoContent(String title, String url) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Content title is required");
        if (url == null || url.isBlank()) throw new IllegalArgumentException("Video URL is required");
        this.title = title;
        this.url = url;
    }
    @Override
    public String getTitle() { return title; }
    public String getUrl() { return url; }
    @Override
    public String getType() { return "VIDEO"; }
    @Override
    public String render() { return getType() + ": " + title + " | " + url; }
}
