package com.education.platform.model;

import java.util.List;

public class Course {
    private final String title;
    private final String teacher;
    private final int price;
    private final List<String> tags;
    private final List<Module> modules;

    public Course(String title, String teacher, int price, List<String> tags, List<Module> modules) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is required");
        if (teacher == null || teacher.isBlank()) throw new IllegalArgumentException("Teacher is required");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (modules == null || modules.isEmpty()) throw new IllegalArgumentException("Add at least one module");
        this.title = title;
        this.teacher = teacher;
        this.price = price;
        this.tags = List.copyOf(tags);
        this.modules = List.copyOf(modules);
    }

    public String getTitle() { return title; }
    public String getTeacher() { return teacher; }
    public int getPrice() { return price; }
    public List<String> getTags() { return tags; }
    public List<Module> getModules() { return modules; }
}
