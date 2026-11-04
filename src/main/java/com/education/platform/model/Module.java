package com.education.platform.model;

import com.education.platform.factory.EducationalContent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Module {
    private final String title;
    private final List<EducationalContent> materials = new ArrayList<>();
    private final List<Assignment> assignments = new ArrayList<>();

    public Module(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Module title is required");
        this.title = title;
    }
    public Module addContent(EducationalContent content) {
        materials.add(Objects.requireNonNull(content));
        return this;
    }
    public Module addAssignment(Assignment assignment) {
        assignments.add(Objects.requireNonNull(assignment));
        return this;
    }
    public String getTitle() { return title; }
    public List<EducationalContent> getMaterials() { return List.copyOf(materials); }
    public List<Assignment> getAssignments() { return List.copyOf(assignments); }
}
