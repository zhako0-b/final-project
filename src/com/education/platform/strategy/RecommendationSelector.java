package com.education.platform.strategy;
import java.util.List;

public class RecommendationSelector {
    public IRecommendationStrategy selectStrategy(
            List<String> interests
    ) {
        boolean hasInterests = interests != null
                && interests.stream().anyMatch(
                interest ->
                        interest != null
                                && !interest.isBlank()
        );

        if (hasInterests) {
            return new TagMatchStrategy();
        }
        return new PopularityStrategy();
    }
}
