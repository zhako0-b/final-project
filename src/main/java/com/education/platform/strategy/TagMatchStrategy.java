package com.education.platform.strategy;

import com.education.platform.model.Course;
import java.util.*;

public class TagMatchStrategy implements IRecommendationStrategy {

    @Override
    public List<Course> recommend(
            List<Course> courses,
            Map<String, Integer> studentCounts,
            List<String> interests) {
        List<Course> result = new ArrayList<>();

        if (interests == null) {
            return result;
        }

        for (Course course : courses) {
            for (String tag : course.getTags()) {
                for (String interest : interests) {
                    if (interest != null && tag.equalsIgnoreCase(interest)) {
                        result.add(course);
                        break;
                    }
                }
                if (result.contains(course)) {
                    break;
                }
            }
        }
        return result;
    }
}

