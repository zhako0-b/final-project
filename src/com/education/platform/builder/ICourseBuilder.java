package com.education.platform.builder;

import com.education.platform.model.Course;

public interface ICourseBuilder {
    ICourseBuilder setTitle(String title);
    Course build();


}
