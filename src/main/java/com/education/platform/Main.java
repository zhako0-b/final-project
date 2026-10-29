package com.education.platform;

import com.education.platform.builder.StandardCourseBuilder;
import com.education.platform.factory.ContentFactory;
import com.education.platform.factory.EducationalContent;
import com.education.platform.factory.TextContentFactory;
import com.education.platform.model.Course;

public class Main {
    public static void main(String[] args) {
        Course course = new StandardCourseBuilder()
                .setTitle("Java Basics")
                .build();

        ContentFactory factory = new TextContentFactory();
        EducationalContent content = factory.createContent("Variables", "int age = 18;");

        System.out.println("Online Education Platform");
        System.out.println("Course: " + course.getTitle());
        System.out.println(content.render());


    }
}
