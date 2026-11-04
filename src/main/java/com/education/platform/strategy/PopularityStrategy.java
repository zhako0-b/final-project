package com.education.platform.strategy;

import com.education.platform.model.Course;
import java.util.*;

public class PopularityStrategy implements IRecommendationStrategy {
    @Override
    public List<Course> recommend(
            List<Course> courses,
            Map<String, Integer> studentCounts,
            List<String> interests) {
        List<Course> result = new ArrayList<>(courses);

        Map<String, Integer> counts =
                studentCounts == null ? Collections.emptyMap() : studentCounts;
        result.sort(
                Comparator.comparingInt(
                        (Course course) ->
                                counts.getOrDefault(course.getTitle(), 0)
                ).reversed()
        );
        return result;
    }
}

