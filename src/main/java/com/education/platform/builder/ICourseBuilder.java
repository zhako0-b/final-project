package com.education.platform.builder;

import com.education.platform.model.Course;
import com.education.platform.model.Module;

public interface ICourseBuilder {
    ICourseBuilder setTitle(String title);
    ICourseBuilder setTeacher(String teacher);
    ICourseBuilder setPrice(int price);
    ICourseBuilder addTag(String tag);
    ICourseBuilder addModule(Module module);
    Course build();
}
