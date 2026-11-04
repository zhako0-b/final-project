package com.education.platform.strategy;

import com.education.platform.model.Course;
import java.util.List;

public class RecommendationContext {
    private IRecommendationStrategy strategy;
    public RecommendationContext(IRecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(IRecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Course> recommendCourses(List<Course> courses) {
        return strategy.recommend(courses);
    }
}
