package com.education.platform.decorator;

import com.education.platform.model.Course;

/** Wraps a Course so it can be decorated. Course has no price field, so the base price is stored here. */
public class BaseCourse implements ICourseComponent {
    private final Course course;
    private final double basePrice;

    public BaseCourse(Course course, double basePrice) {
        this.course = course;
        this.basePrice = basePrice;
    }

    public Course getCourse() { return course; }

    @Override
    public String getDescription() { return course.getTitle(); }

    @Override
    public double getPrice() { return basePrice; }
}
