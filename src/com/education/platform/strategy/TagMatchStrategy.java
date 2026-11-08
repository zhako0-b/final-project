package com.education.platform.strategy;

public class TagMatchStrategy implements IRecommendationStrategy {

    @Override
    public void recommend() {
        System.out.println("Recommending courses by matching tags");
    }
}