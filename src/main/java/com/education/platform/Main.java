package com.education.platform;

import com.education.platform.builder.StandardCourseBuilder;
import com.education.platform.factory.*;
import com.education.platform.model.Assignment;
import com.education.platform.model.Course;
import com.education.platform.model.Module;

public class Main {
    public static void main(String[] args) {
        System.out.println("Online Education Platform - Builder and Factory Method");

        // Factory Method: каждая фабрика создаёт свой тип материала.
        ContentFactory textFactory = new TextContentFactory();
        ContentFactory videoFactory = new VideoContentFactory();
        ContentFactory quizFactory = new QuizContentFactory();
        Module basics = new Module("Java fundamentals")
                .addContent(textFactory.createContent("Variables", "int age = 18;"))
                .addContent(videoFactory.createContent("Introduction", "https://example.com/java-intro"))
                .addContent(quizFactory.createContent("Quick quiz", "Which type stores a whole number?"))
                .addAssignment(new Assignment("First program", "Print your name"));
        Module practice = new Module("Practice")
                .addContent(textFactory.createContent("Loops", "for (int i = 0; i < 3; i++)"))
                .addAssignment(new Assignment("Numbers", "Print numbers from 1 to 10"));

        // Builder: пошаговая сборка курса из готовых модулей.
        Course course = new StandardCourseBuilder()
                .setTitle("Java Basics")
                .setTeacher("Aida")
                .setPrice(100)
                .addTag("java")
                .addTag("beginner")
                .addModule(basics)
                .addModule(practice)
                .build();

        System.out.println("Course: " + course.getTitle());
        System.out.println("Teacher: " + course.getTeacher());
        System.out.println("Price: " + course.getPrice());
        System.out.println("Tags: " + course.getTags());
        for (Module module : course.getModules()) {
            System.out.println("Module: " + module.getTitle());
            module.getMaterials().forEach(content -> System.out.println("  " + content.render()));
            module.getAssignments().forEach(task -> System.out.println(
                    "  Assignment: " + task.getTitle() + " | " + task.getDescription()));
        }
        // Общий метод фабрики вызывает переопределённый createContent().
        System.out.println("Preview: " + textFactory.preview("Summary", "Course introduction"));
    }
}
