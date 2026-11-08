package com.education.platform.factory;

public class QuizContentFactory extends ContentFactory {
    @Override
    public EducationalContent createContent(String title, String data) {
        return new QuizContent(title, data);
    }
}
