package com.education.platform.strategy;

public class PopularityStrategy implements IRecommendationStrategy {

    @Override
    public void recommend() {
        System.out.println("Recommending courses by popularity");
    }
}