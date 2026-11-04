package com.education.platform.factory;

public abstract class ContentFactory {
    public abstract EducationalContent createContent(String title, String data);

    // Общий алгоритм использует Factory Method конкретной фабрики.
    public String preview(String title, String data) {
        return createContent(title, data).render();
    }
}
