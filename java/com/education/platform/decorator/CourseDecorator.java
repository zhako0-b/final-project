package com.education.platform.decorator;

public abstract class CourseDecorator implements ICourseComponent {
    protected final ICourseComponent wrapped;

    protected CourseDecorator(ICourseComponent wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getDescription() { return wrapped.getDescription(); }

    @Override
    public double getPrice() { return wrapped.getPrice(); }
}
