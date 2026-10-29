package com.education.platform.factory;

public class QuizContent implements EducationalContent {
    private final String title;
    private final String question;

    public QuizContent(String title, String question) {
        this.title = title;
        this.question = question;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getQuestion() {
        return question;
    }

    @Override
    public String getType() {
        return "QUIZ";
    }

    @Override
    public String render() {
        return getType() + ": " + title;
    }


}
