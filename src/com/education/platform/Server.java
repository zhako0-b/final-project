package com.education.platform;

import com.education.platform.builder.StandardCourseBuilder;
import com.education.platform.decorator.*;
import com.education.platform.facade.PlatformFacade;
import com.education.platform.factory.*;
import com.education.platform.model.Assignment;
import com.education.platform.model.Course;
import com.education.platform.model.Module;
import com.education.platform.observer.*;
import com.education.platform.strategy.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/** Backend: a small HTTP API over the six pattern-based classes, plus the web/ frontend. */
public class Server {
    interface H { String run(Map<String, String> p) throws Exception; }

    static final Map<String, Course> courses = new LinkedHashMap<>();
    static final Map<String, CourseSubject> subjects = new HashMap<>();
    static final Map<String, Integer> students = new HashMap<>();
    static final Map<String, Map<String, ICourseComponent>> enrolled = new HashMap<>();
    static final List<String> inbox = new ArrayList<>();
    static final PlatformFacade facade = new PlatformFacade();
    record Info(String description, int hours, String bio) {}
    static final Map<String, Info> info = new HashMap<>();

    public static void main(String[] args) throws IOException {
        seed();
        HttpServer sv = HttpServer.create(new InetSocketAddress(8080), 0);
        sv.createContext("/", ex -> {
            byte[] b = Files.readAllBytes(Path.of("web", "index.html"));
            ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            ex.sendResponseHeaders(200, b.length);
            try (OutputStream o = ex.getResponseBody()) { o.write(b); }
        });
        api(sv, "/api/courses", p -> {
            String input = p.getOrDefault("interests", "");
            List<String> interests = Arrays.stream(input.split(","))
                    .map(String::trim).filter(value -> !value.isBlank()).collect(Collectors.toList());
            String mode = p.getOrDefault("strategy", "auto");
            IRecommendationStrategy strategy;
            if ("popular".equalsIgnoreCase(mode)) {
                strategy = new PopularityStrategy();
            } else if ("tags".equalsIgnoreCase(mode)) {
                strategy = new TagMatchStrategy();
            } else {
                strategy = new RecommendationSelector().selectStrategy(interests);
            }
            List<Course> result = new RecommendationContext(strategy)
                    .recommendCourses(new ArrayList<>(courses.values()), students, interests);
            String search = p.getOrDefault("search", "").trim().toLowerCase(Locale.ROOT);
            String tagFilter = p.getOrDefault("tag", "").trim().toLowerCase(Locale.ROOT);
            int maxPrice = Integer.parseInt(p.getOrDefault("maxPrice", "1000000"));
            result.removeIf(course -> {
                boolean matchesSearch = course.getTitle().toLowerCase(Locale.ROOT).contains(search)
                        || course.getTeacher().toLowerCase(Locale.ROOT).contains(search)
                        || course.getTags().stream().anyMatch(tag -> tag.toLowerCase(Locale.ROOT).contains(search));
                boolean matchesTag = tagFilter.isBlank()
                        || course.getTags().stream().anyMatch(tag -> tag.equalsIgnoreCase(tagFilter));
                boolean matchesPrice = course.getPrice() <= maxPrice;
                return !matchesSearch || !matchesTag || !matchesPrice;
            });
            return "[" + result.stream().map(Server::courseJson).collect(Collectors.joining(",")) + "]";
        });

        api(sv, "/api/quote", p -> {
            ICourseComponent o = offer(p);
            return "{\"description\":" + s(o.getDescription()) + ",\"price\":" + o.getPrice() + "}";
        });
        api(sv, "/api/purchase", p -> {
            String st = need(p, "student"), t = need(p, "course");
            ICourseComponent o = offer(p);
            boolean ok = facade.purchaseCourse(st, o);
            if (ok) {
                Map<String, ICourseComponent> mine = enrolled.computeIfAbsent(st, k -> new LinkedHashMap<>());
                boolean first = !mine.containsKey(t);
                mine.put(t, o);
                CourseSubject sub = subjects.get(t);
                if (first) {
                    students.merge(t, 1, Integer::sum);
                    sub.attach(new StudentNotifier(st, inbox));
                }
                sub.notifyObservers(st + " enrolled in the course");
            }
            return "{\"ok\":" + ok + ",\"description\":" + s(o.getDescription()) + ",\"total\":" + o.getPrice() + "}";
        });
        api(sv, "/api/my", p -> {
            Map<String, ICourseComponent> m = enrolled.getOrDefault(need(p, "student"), new LinkedHashMap<>());
            return "[" + m.entrySet().stream().map(e -> "{\"course\":" + s(e.getKey()) + ",\"description\":"
                    + s(e.getValue().getDescription()) + ",\"price\":" + e.getValue().getPrice() + "}")
                    .collect(Collectors.joining(",")) + "]";
        });
        api(sv, "/api/complete", p -> {
            String st = need(p, "student"), t = need(p, "course");
            ICourseComponent o = enrolled.getOrDefault(st, new LinkedHashMap<>()).get(t);
            if (o == null) throw new IllegalArgumentException("You are not enrolled in this course");
            if (!o.getDescription().contains("Certificate"))
                throw new IllegalArgumentException("This course was bought without a certificate");
            String file = facade.completeCourse(st, o);
            subjects.get(t).notifyObservers(st + " received certificate " + file);
            return "{\"file\":" + s(file) + "}";
        });
        api(sv, "/api/create", p -> {
            ContentFactory f = ContentFactory.forType(p.getOrDefault("type", "text"));
            StandardCourseBuilder b = new StandardCourseBuilder().setTitle(p.get("title"))
                    .setTeacher(p.get("teacher")).setPrice(Integer.parseInt(p.getOrDefault("price", "0")));
            for (String t : p.getOrDefault("tags", "").split(",")) if (!t.isBlank()) b.addTag(t.trim());
            Module m = new Module(p.getOrDefault("module", "Module 1"))
                    .addContent(f.createContent(p.get("contentTitle"), p.get("contentData")));
            Course c = b.addModule(m).build();
            info.put(c.getTitle(), new Info(p.getOrDefault("description", ""),
                    Integer.parseInt(p.getOrDefault("hours", "0")), p.getOrDefault("bio", "")));
            publish(c);
            return courseJson(c);
        });
        api(sv, "/api/announce", p -> {
            CourseSubject sub = subjects.get(p.get("course"));
            if (sub == null) throw new IllegalArgumentException("Unknown course");
            sub.publishAnnouncement(need(p, "text"));
            return "{\"ok\":true}";
        });
        api(sv, "/api/grade", p -> {
            String course = need(p, "course");
            String student = need(p, "student");
            int grade = Integer.parseInt(need(p, "grade"));
            CourseSubject subject = subjects.get(course);
            if (subject == null) throw new IllegalArgumentException("Unknown course");
            if (!enrolled.getOrDefault(student, Collections.emptyMap()).containsKey(course))
                throw new IllegalArgumentException("Student is not enrolled in this course");
            subject.gradeAssignment(student, grade);
            return "{\"ok\":true}";
        });
        api(sv, "/api/notifications", p -> {
            String who = p.getOrDefault("user", "") + "|";
            return "[" + inbox.stream().filter(x -> x.startsWith(who))
                    .map(x -> s(x.substring(who.length()))).collect(Collectors.joining(",")) + "]";
        });
        sv.start();
        System.out.println("Open http://localhost:8080");
    }

    static void seed() {
        ContentFactory text = new TextContentFactory();
        ContentFactory video = new VideoContentFactory();
        ContentFactory quiz = new QuizContentFactory();
        Object[][] rows = {
            {"Java Basics", "Aida", 100, 120, 24, "Основы Java: переменные, циклы и ООП.", "Преподаватель Java.", "java,beginner,programming", "Java Fundamentals"},
            {"Python Programming", "Marat", 90, 110, 30, "Изучение Python с нуля.", "Преподаватель программирования.", "python,beginner,programming", "Python Fundamentals"},
            {"Web Design", "Dana", 60, 80, 16, "HTML, CSS и адаптивная вёрстка.", "Веб-дизайнер.", "web,design,beginner", "HTML and CSS"},
            {"Data Science", "Marat", 150, 45, 40, "Анализ данных с помощью Python.", "Специалист по анализу данных.", "python,data,advanced", "Data Analysis"},
            {"Cybersecurity", "Alex", 140, 70, 35, "Основы защиты компьютерных систем.", "Специалист по кибербезопасности.", "security,network,advanced", "Security Fundamentals"},
            {"UI/UX Design", "Dana", 85, 95, 22, "Проектирование удобных интерфейсов.", "UI/UX-дизайнер.", "design,web,beginner", "User Experience"},
            {"Java Advanced", "Aida", 180, 55, 42, "Продвинутые темы Java и архитектура приложений.", "Преподаватель Java.", "java,advanced,programming", "Advanced Java"},
            {"Machine Learning", "Marat", 200, 65, 48, "Введение в машинное обучение.", "Специалист по машинному обучению.", "python,data,advanced", "Machine Learning Basics"},
            {"Mobile Development", "Alex", 130, 75, 32, "Разработка мобильных приложений.", "Разработчик мобильных приложений.", "mobile,java,programming", "Mobile Applications"},
            {"Digital Marketing", "Dana", 70, 100, 20, "Основы продвижения продуктов в интернете.", "Специалист по цифровому маркетингу.", "marketing,business,beginner", "Marketing Fundamentals"}
        };
        for (Object[] r : rows) {
            addDemoCourse((String) r[0], (String) r[1], (int) r[2], (int) r[3], (int) r[4], (String) r[5],
                    (String) r[6], List.of(((String) r[7]).split(",")), (String) r[8], text, video, quiz);
        }
    }

    static void addDemoCourse(String title, String teacher, int price, int studentCount, int hours,
                              String description, String bio, List<String> tags, String moduleTitle,
                              ContentFactory text, ContentFactory video, ContentFactory quiz) {
        Module firstModule = new Module(moduleTitle)
                .addContent(text.createContent("Introduction", "Introduction to " + title))
                .addContent(video.createContent("Video Lesson", "https://example.com/lesson"))
                .addContent(quiz.createContent("Knowledge Check", "What did you learn?"))
                .addAssignment(new Assignment("Practical Task", "Complete a practical task about " + title));
        Module secondModule = new Module("Practice")
                .addContent(text.createContent("Practice Guide", "Practice the skills from " + title))
                .addAssignment(new Assignment("Final Project", "Create a small project using your new skills"));
        StandardCourseBuilder builder = new StandardCourseBuilder().setTitle(title).setTeacher(teacher).setPrice(price);
        for (String tag : tags) builder.addTag(tag);
        Course course = builder.addModule(firstModule).addModule(secondModule).build();
        publish(course);
        students.put(title, studentCount);
        info.put(title, new Info(description, hours, bio));
    }

    static void publish(Course c) {
        courses.put(c.getTitle(), c);
        CourseSubject sub = new CourseSubject(c.getTitle());
        sub.attach(new TeacherNotifier(c.getTeacher(), inbox));
        subjects.put(c.getTitle(), sub);
    }

    static ICourseComponent offer(Map<String, String> p) {
        Course c = courses.get(p.get("course"));
        if (c == null) throw new IllegalArgumentException("Unknown course");
        ICourseComponent o = new BaseCourse(c);
        if ("1".equals(p.get("cert"))) o = new CertificatedCourseDecorator(o);
        if ("1".equals(p.get("mentor"))) o = new MentorshipDecorator(o);
        return o;
    }

    static String courseJson(Course c) {
        String mods = c.getModules().stream().map(m -> "{\"title\":" + s(m.getTitle()) + ",\"materials\":["
                + m.getMaterials().stream().map(x -> "{\"type\":" + s(x.getType()) + ",\"title\":" + s(x.getTitle()) + "}")
                .collect(Collectors.joining(","))
                + "],\"assignments\":[" + m.getAssignments().stream().map(a -> s(a.getTitle()))
                .collect(Collectors.joining(",")) + "]}").collect(Collectors.joining(","));
        return "{\"title\":" + s(c.getTitle()) + ",\"teacher\":" + s(c.getTeacher()) + ",\"price\":" + c.getPrice()
                + ",\"tags\":[" + c.getTags().stream().map(Server::s).collect(Collectors.joining(","))
                + "],\"students\":" + students.getOrDefault(c.getTitle(), 0) + extra(c) + ",\"modules\":[" + mods + "]}";
    }

    static String extra(Course c) {
        Info i = info.getOrDefault(c.getTitle(), new Info("", 0, ""));
        return ",\"description\":" + s(i.description()) + ",\"hours\":" + i.hours() + ",\"bio\":" + s(i.bio());
    }

    static String s(String x) {
        return "\"" + x.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ") + "\"";
    }

    static String need(Map<String, String> p, String k) {
        String v = p.get(k);
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Missing: " + k);
        return v;
    }

    static Map<String, String> params(HttpExchange ex) {
        Map<String, String> m = new HashMap<>();
        String q = ex.getRequestURI().getRawQuery();
        if (q != null) for (String part : q.split("&")) {
            String[] kv = part.split("=", 2);
            m.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                    kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "");
        }
        return m;
    }

    static void api(HttpServer sv, String path, H h) {
        sv.createContext(path, ex -> {
            int code = 200;
            String out;
            try { out = h.run(params(ex)); }
            catch (Exception e) { code = 400; out = "{\"error\":" + s(String.valueOf(e.getMessage())) + "}"; }
            byte[] b = out.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            ex.sendResponseHeaders(code, b.length);
            try (OutputStream o = ex.getResponseBody()) { o.write(b); }
        });
    }
}