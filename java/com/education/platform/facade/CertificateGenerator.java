package com.education.platform.facade;

/** Simulated PDF certificate generator. */
public class CertificateGenerator {
    public String generate(String studentName, String courseTitle) {
        String file = "certificate_" + studentName.replace(" ", "_") + ".pdf";
        System.out.println("[Certificate] Generated " + file + " for course '" + courseTitle + "'");
        return file;
    }
}
