package com.education.platform.decorator;

public class MentorshipDecorator extends CourseDecorator {
    private static final double MENTORSHIP_PRICE = 50.0;

    public MentorshipDecorator(ICourseComponent wrapped) {
        super(wrapped);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Mentorship";
    }

    @Override
    public double getPrice() {
        return wrapped.getPrice() + MENTORSHIP_PRICE;
    }
}
