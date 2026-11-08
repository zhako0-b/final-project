package com.education.platform;

import com.education.platform.builder.StandardCourseBuilder;
import com.education.platform.decorator.*;
import com.education.platform.facade.PlatformFacade;
import com.education.platform.factory.ContentFactory;
import com.education.platform.model.Assignment;
import com.education.platform.model.Course;
import com.education.platform.model.Module;
import com.education.platform.observer.*;
import com.education.platform.strategy.*;
import java.util.*;

/** Console demo: Factory -> Builder -> Decorator -> Strategy -> Observer -> Facade. */
public class Main {
    public static void main(String[] args) {
        // 1. Factory Method: content is created through factories chosen by type
        ContentFactory text = ContentFactory.forType("text");
        ContentFactory video = ContentFactory.forType("video");
        ContentFactory quiz = ContentFactory.forType("quiz");
        System.out.println(text.preview("Variables", "int a = 1;"));

        // 2. Builder: step-by-step course assembly
        Module module = new Module("Fundamentals")
                .addContent(text.createContent("Variables", "int a = 1;"))
                .addContent(video.createContent("Introduction", "https://example.com/java"))
                .addContent(quiz.createContent("Quick quiz", "Which type stores a whole number?"))
                .addAssignment(new Assignment("Task 1", "Print your name"));
        Course course = new StandardCourseBuilder().setTitle("Java Basics").setTeacher("Aida")
                .setPrice(100).addTag("java").addTag("beginner").addModule(module).build();
        System.out.println("Course built: " + course.getTitle() + " ($" + course.getPrice() + ")");

        // 3. Decorator: optional extras, price recalculated
        ICourseComponent offer = new MentorshipDecorator(new CertificatedCourseDecorator(new BaseCourse(course)));
        System.out.println(offer.getDescription() + " = $" + offer.getPrice());

        // 4. Strategy: switch the algorithm at runtime
        Map<String, Integer> counts = Map.of("Java Basics", 120);
        RecommendationContext rec = new RecommendationContext(new PopularityStrategy());
        System.out.println("Popular: " + rec.recommendCourses(List.of(course), counts, List.of()).size());
        rec.setStrategy(new TagMatchStrategy());
        System.out.println("By tags: " + rec.recommendCourses(List.of(course), counts, List.of("java")).size());

        // 5. Observer: notify subscribers about course events
        List<String> inbox = new ArrayList<>();
        CourseSubject subject = new CourseSubject(course.getTitle());
        subject.attach(new StudentNotifier("Aigerim", inbox));
        subject.attach(new TeacherNotifier("Aida", inbox));
        subject.publishAnnouncement("Lesson 2 is published");
        subject.submitAssignment("Aigerim");
        subject.gradeAssignment("Aigerim", 95);

        // 6. Facade: payment + enrollment + certificate in simple calls
        PlatformFacade platform = new PlatformFacade();
        if (platform.purchaseCourse("Aigerim", offer)) platform.completeCourse("Aigerim", offer);
    }
}
