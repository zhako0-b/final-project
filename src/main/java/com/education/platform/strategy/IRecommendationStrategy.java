package com.education.platform.strategy;

import com.education.platform.model.Course;
import java.util.List;

public interface IRecommendationStrategy {
    List<Course> recommend(List<Course> courses);
}
