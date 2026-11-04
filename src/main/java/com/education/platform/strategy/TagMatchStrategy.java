package com.education.platform.strategy;

import com.education.platform.model.Course;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TagMatchStrategy implements IRecommendationStrategy {
    private final String keyword;

    public TagMatchStrategy(String keyword) {
        this.keyword = keyword.toLowerCase(Locale.ROOT);
    }

    @Override
    public List<Course> recommend(List<Course> courses) {
        List<Course> result = new ArrayList<>();

        for (Course course : courses) {
            if (course.getTitle().toLowerCase(Locale.ROOT).contains(keyword)) {
                result.add(course);
            }
        }
        return result;
    }
}
