package com.education.platform.builder;

import com.education.platform.model.Course;

public class StandardCourseBuilder implements ICourseBuilder {
    private String title;

    @Override
    public StandardCourseBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Course build() {
        return new Course(title);
    }

}
