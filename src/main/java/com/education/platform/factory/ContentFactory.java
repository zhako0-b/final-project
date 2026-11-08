package com.education.platform.factory;

public abstract class ContentFactory {
    public static ContentFactory forType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Content type is required");
        }
        return switch (type.strip().toLowerCase(java.util.Locale.ROOT)) {
            case "text" -> new TextContentFactory();
            case "video" -> new VideoContentFactory();
            case "quiz" -> new QuizContentFactory();
            default -> throw new IllegalArgumentException("Unknown content type: " + type);
        };
    }

    public abstract EducationalContent createContent(String title, String data);

    // Общий алгоритм использует Factory Method конкретной фабрики.
    public String preview(String title, String data) {
        return createContent(title, data).render();
    }
}
