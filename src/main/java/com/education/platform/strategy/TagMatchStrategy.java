package com.education.platform.strategy;
import com.education.platform.model.Course;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class TagMatchStrategy implements IRecommendationStrategy {

    @Override
    public List<Course> recommend(
            List<Course> courses,
            Map<String, Integer> studentCounts,
            List<String> interests
    ) {
        List<Course> result = new ArrayList<>();

        if (interests == null || interests.isEmpty()) {
            return result;
        }
        Set<String> normalizedInterests = new HashSet<>();

        for (String interest : interests) {
            if (interest != null && !interest.isBlank()) {
                normalizedInterests.add(
                        interest.trim().toLowerCase(Locale.ROOT)
                );
            }
        }

        for (Course course : courses) {
            if (matchScore(course, normalizedInterests) > 0) {
                result.add(course);
            }
        }

        result.sort(
                Comparator
                        .comparingInt(
                                (Course course) ->
                                        matchScore(course, normalizedInterests)
                        )
                        .reversed()
                        .thenComparing(
                                Comparator.comparingInt(
                                        (Course course) ->
                                                studentCounts == null
                                                        ? 0
                                                        : studentCounts.getOrDefault(
                                                        course.getTitle(),
                                                        0
                                                )
                                ).reversed()
                        )
        );
        return result;
    }

    private int matchScore(
            Course course,
            Set<String> interests
    ) {
        int score = 0;

        for (String tag : course.getTags()) {
            if (interests.contains(
                    tag.trim().toLowerCase(Locale.ROOT)
            )) {
                score++;
            }
        }
        return score;
    }
}
