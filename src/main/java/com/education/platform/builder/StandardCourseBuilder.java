package com.education.platform.builder;

import com.education.platform.model.Course;
import com.education.platform.model.Module;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StandardCourseBuilder implements ICourseBuilder {
    private String title;
    private String teacher;
    private int price;
    private final List<String> tags = new ArrayList<>();
    private final List<Module> modules = new ArrayList<>();

    @Override
    public StandardCourseBuilder setTitle(String title) { this.title = title; return this; }
    @Override
    public StandardCourseBuilder setTeacher(String teacher) { this.teacher = teacher; return this; }
    @Override
    public StandardCourseBuilder setPrice(int price) { this.price = price; return this; }
    @Override
    public StandardCourseBuilder addTag(String tag) {
        if (tag == null || tag.isBlank()) throw new IllegalArgumentException("Tag is required");
        String normalized = tag.strip();
        if (tags.stream().noneMatch(normalized::equalsIgnoreCase)) {
            tags.add(normalized);
        }
        return this;
    }
    @Override
    public StandardCourseBuilder addModule(Module module) {
        modules.add(Objects.requireNonNull(module));
        return this;
    }
    @Override
    public Course build() { return new Course(title, teacher, price, tags, modules); }
}
