package com.education.platform.decorator;

import com.education.platform.model.Course;

public class BaseCourse implements ICourseComponent {
    private final Course course;

    public BaseCourse(Course course) { this.course = course; }

    @Override public String getDescription() { return course.getTitle(); }
    @Override public double getPrice() { return course.getPrice(); }
}
