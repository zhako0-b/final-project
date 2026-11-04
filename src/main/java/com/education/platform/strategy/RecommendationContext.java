package com.education.platform.strategy;

import com.education.platform.model.Course;
import java.util.List;
import java.util.Map;

public class RecommendationContext {
    private IRecommendationStrategy strategy;
    public RecommendationContext(IRecommendationStrategy strategy) {
        setStrategy(strategy);
    }

    public void setStrategy(IRecommendationStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }
        this.strategy = strategy;
    }

    public List<Course> recommendCourses(
            List<Course> courses,
            Map<String, Integer> studentCounts,
            List<String> interests) {
        return strategy.recommend(courses, studentCounts, interests);
    }
}
