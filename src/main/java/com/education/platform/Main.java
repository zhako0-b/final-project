package com.education.platform;

import com.education.platform.builder.ICourseBuilder;
import com.education.platform.builder.StandardCourseBuilder;
import com.education.platform.decorator.BaseCourse;
import com.education.platform.decorator.CertificatedCourseDecorator;
import com.education.platform.decorator.ICourseComponent;
import com.education.platform.decorator.MentorshipDecorator;
import com.education.platform.facade.PlatformFacade;
import com.education.platform.factory.ContentFactory;
import com.education.platform.factory.EducationalContent;
import com.education.platform.factory.QuizContentFactory;
import com.education.platform.factory.TextContentFactory;
import com.education.platform.factory.VideoContentFactory;
import com.education.platform.model.Course;
import com.education.platform.strategy.PopularityStrategy;
import com.education.platform.strategy.RecommendationContext;
import com.education.platform.strategy.TagMatchStrategy;

/*
 * End-to-end demo: Factory -> Builder -> Decorator -> Strategy -> Observer -> Facade.
 */
public class Main {
    public static void main(String[] args) {
        // 1. FACTORY METHOD - each factory creates its own content type
        ContentFactory videoFactory = new VideoContentFactory();
        ContentFactory textFactory = new TextContentFactory();
        ContentFactory quizFactory = new QuizContentFactory();
        EducationalContent[] contents = {
                videoFactory.createContent("Intro to Java", "https://example.com/intro"),
                textFactory.createContent("OOP Basics", "Classes and objects"),
                quizFactory.createContent("Java Quiz 1", "What is a class?")
        };
        for (EducationalContent c : contents) {
            System.out.println("Content created -> " + c.render());
        }

        // 2. BUILDER
        ICourseBuilder builder = new StandardCourseBuilder();
        Course java = builder.setTitle("Java for Beginners").build();
        System.out.println("Course built: " + java.getTitle());

        // 3. DECORATOR - options added dynamically, price recalculated
        ICourseComponent offer = new BaseCourse(java, 100.0);
        System.out.println("Base: " + offer.getDescription() + " = $" + offer.getPrice());
        offer = new CertificatedCourseDecorator(offer);
        offer = new MentorshipDecorator(offer);
        System.out.println("Decorated: " + offer.getDescription() + " = $" + offer.getPrice());

        // 4. STRATEGY - switch algorithm at runtime
        RecommendationContext rec = new RecommendationContext(new PopularityStrategy());
        rec.recommendCourses();
        rec.setStrategy(new TagMatchStrategy());
        rec.recommendCourses();

        // 5. OBSERVER (Student 2) - TODO: notify subscribers when it is ready

        // 6. FACADE - payment + enrollment + certificate
        PlatformFacade platform = new PlatformFacade();
        if (platform.purchaseCourse("Aigerim", offer)) {
            platform.completeCourse("Aigerim", offer);
        }
    }
}
