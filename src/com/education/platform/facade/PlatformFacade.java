package com.education.platform.facade;

import com.education.platform.decorator.ICourseComponent;

/** Facade: one simple method hides payment, enrollment and certificate steps. */
public class PlatformFacade {
    private final PaymentSubSystem payment = new PaymentSubSystem();
    private final CertificateGenerator certificates = new CertificateGenerator();

    public boolean purchaseCourse(String studentName, ICourseComponent course) {
        if (!payment.charge(studentName, course.getPrice())) {
            System.out.println("[Facade] Payment failed.");
            return false;
        }
        System.out.println("[Enrollment] " + studentName + " enrolled in: " + course.getDescription());
        return true;
    }

    public String completeCourse(String studentName, ICourseComponent course) {
        return certificates.generate(studentName, course.getDescription());
    }
}
