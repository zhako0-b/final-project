package com.education.platform.strategy;

import com.education.platform.model.Course;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PopularityStrategy implements IRecommendationStrategy {
    @Override
    public List<Course> recommend(List<Course> courses) {

        List<Course> result = new ArrayList<>(courses);
        result.sort(Comparator.comparing(Course::getTitle));

        return result;
    }
}
