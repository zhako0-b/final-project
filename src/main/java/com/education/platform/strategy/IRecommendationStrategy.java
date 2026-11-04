package com.education.platform.strategy;

import com.education.platform.model.Course;
import java.util.List;
import java.util.Map;

public interface IRecommendationStrategy {
    List<Course> recommend(
            List<Course> courses,
            Map<String, Integer> studentCounts,
            List<String> interests
    );
}
