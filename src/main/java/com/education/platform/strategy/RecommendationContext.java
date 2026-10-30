package com.education.platform.strategy;

public class RecommendationContext {
    private IRecommendationStrategy strategy;

    public RecommendationContext(IRecommendationStrategy strategy) {
        this.strategy = strategy;
    }
    public void setStrategy(IRecommendationStrategy strategy) {
        this.strategy = strategy;
    }
    public void recommendCourses() {
        strategy.recommend();
    }
}