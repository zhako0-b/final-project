package com.education.platform.strategy;
import java.util.List;

public class RecommendationSelector {
    public IRecommendationStrategy selectStrategy(List<String> interests) {
        if (interests != null && !interests.isEmpty()) {
            return new TagMatchStrategy();
        }
        return new PopularityStrategy();
    }
}
