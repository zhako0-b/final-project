package com.education.platform.decorator;

public class CertificatedCourseDecorator extends CourseDecorator {
    private static final double CERTIFICATE_PRICE = 20.0;

    public CertificatedCourseDecorator(ICourseComponent wrapped) {
        super(wrapped);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Certificate";
    }

    @Override
    public double getPrice() {
        return wrapped.getPrice() + CERTIFICATE_PRICE;
    }
}
