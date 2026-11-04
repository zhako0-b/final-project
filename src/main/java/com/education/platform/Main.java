package com.education.platform;

import com.education.platform.builder.StandardCourseBuilder;
import com.education.platform.factory.*;
import com.education.platform.model.Assignment;
import com.education.platform.model.Course;
import com.education.platform.model.Module;
import com.education.platform.strategy.*;
import com.education.platform.observer.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Online Education Platform");

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
            module.getMaterials().forEach(content ->
                    System.out.println("  " + content.render()));
            module.getAssignments().forEach(task ->
                    System.out.println("  Assignment: " + task.getTitle()
                            + " | " + task.getDescription()));
        }
        System.out.println("Preview: " +
                textFactory.preview("Summary", "Course introduction"));

        System.out.println("\n--- Strategy Pattern ---");
        Course python = new StandardCourseBuilder()
                .setTitle("Python Basics")
                .setTeacher("John")
                .addTag("python")
                .addModule(new Module("Python fundamentals"))
                .build();

        List<Course> courses = Arrays.asList(course, python);
        Map<String, Integer> counts = Map.of(
                "Java Basics", 120,
                "Python Basics", 200
        );
        RecommendationContext context =
                new RecommendationContext(new PopularityStrategy());

        System.out.println("Popular courses:");
        for (Course c : context.recommendCourses(courses, counts, List.of("java"))) {
            System.out.println(c.getTitle());
        }

        context.setStrategy(new TagMatchStrategy());
        System.out.println("Courses by tags:");
        for (Course c : context.recommendCourses(courses, counts, List.of("java"))) {
            System.out.println(c.getTitle());
        }

        System.out.println("\n--- Observer Pattern ---");

        CourseSubject subject = new CourseSubject();
        StudentNotifier alex = new StudentNotifier("Alex");
        StudentNotifier dana = new StudentNotifier("Dana");
        TeacherNotifier aida = new TeacherNotifier("Aida");

        subject.addObserver(alex);
        subject.addObserver(dana);
        subject.addObserver(aida);

        subject.publishAnnouncement("New lesson available!");
        subject.submitAssignment("Alex");
        subject.gradeAssignment("Alex", 95);

        subject.removeObserver(alex);
        subject.publishAnnouncement("Final exam tomorrow!");
    }
}
