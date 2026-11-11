package com.education.platform;

import com.education.platform.builder.StandardCourseBuilder;
import com.education.platform.decorator.*;
import com.education.platform.factory.ContentFactory;
import com.education.platform.factory.EducationalContent;
import com.education.platform.model.Assignment;
import com.education.platform.model.Course;
import com.education.platform.model.Module;
import com.education.platform.observer.*;
import com.education.platform.strategy.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Desktop (Swing) interface. Uses the pattern classes directly and shares demo data with Server. */
public class Gui extends JFrame {
    private final JTextField who = new JTextField("Aigerim", 10);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Курс", "Преподаватель", "Цена, $", "Часов", "Студентов"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField search = new JTextField(12), interests = new JTextField(14);
    private final JLabel strategyLabel = new JLabel(" ");
    private final JTextArea details = new JTextArea();
    private final JCheckBox cert = new JCheckBox("Сертификат (+$20)"), mentor = new JCheckBox("Личный наставник (+$50)");
    private final JLabel total = new JLabel("Итого: —");
    private List<Course> shown = new ArrayList<>();
    private List<String> mineKeys = new ArrayList<>();
    private final DefaultListModel<String> mineModel = new DefaultListModel<>();
    private final JList<String> mineList = new JList<>(mineModel);
    private final JTextArea inbox = new JTextArea(), teacherInbox = new JTextArea();
    private final JComboBox<String> announceCourse = new JComboBox<>(), gradeCourse = new JComboBox<>();
    private final JTextField tTitle = new JTextField(), tTeacher = new JTextField("Aida"), tPrice = new JTextField("50"),
            tHours = new JTextField("10"), tTags = new JTextField(), tDesc = new JTextField(), tBio = new JTextField(),
            lTitle = new JTextField("Урок 1"), lData = new JTextField("Введение в курс"),
            announceText = new JTextField(20), gradeStudent = new JTextField("Aigerim", 10);
    private final JComboBox<String> type = new JComboBox<>(new String[]{"text", "video", "quiz"});
    private final JSpinner gradeValue = new JSpinner(new SpinnerNumberModel(90, 0, 100, 1));

    public static void main(String[] args) {
        Server.seed();
        SwingUtilities.invokeLater(() -> new Gui().setVisible(true));
    }

    public Gui() {
        super("EduPlatform — онлайн-курсы");
        UIManager.getDefaults().keySet().stream().filter(k -> UIManager.get(k) instanceof Font)
                .collect(Collectors.toList()).forEach(k -> UIManager.put(k, new Font("SansSerif", Font.PLAIN, 14)));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1080, 700);
        setLocationRelativeTo(null);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(new JLabel("Ваше имя:"));
        top.add(who);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Курсы", coursesPanel());
        tabs.addTab("Мой кабинет", studentPanel());
        tabs.addTab("Преподаватель", teacherPanel());
        tabs.addChangeListener(e -> refreshAll());
        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        refreshAll();
    }

    // ---------------- panels ----------------
    private JPanel coursesPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton find = new JButton("Найти");
        find.addActionListener(e -> refreshCourses());
        filters.add(new JLabel("Поиск:")); filters.add(search);
        filters.add(new JLabel("Интересы (java, web):")); filters.add(interests);
        filters.add(find); filters.add(strategyLabel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) updateDetails(); });
        details.setEditable(false); details.setLineWrap(true); details.setWrapStyleWord(true);
        cert.addActionListener(e -> updateDetails());
        mentor.addActionListener(e -> updateDetails());
        JButton buy = new JButton("Купить курс");
        buy.addActionListener(e -> buy());
        total.setFont(total.getFont().deriveFont(Font.BOLD, 18f));
        JPanel opts = new JPanel(new GridLayout(0, 1, 4, 4));
        opts.setBorder(BorderFactory.createTitledBorder("Дополнительно"));
        opts.add(cert); opts.add(mentor); opts.add(total); opts.add(buy);
        JPanel right = new JPanel(new BorderLayout(6, 6));
        right.add(new JScrollPane(details), BorderLayout.CENTER);
        right.add(opts, BorderLayout.SOUTH);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), right);
        split.setDividerLocation(540);
        p.add(filters, BorderLayout.NORTH);
        p.add(split, BorderLayout.CENTER);
        return p;
    }

    private JPanel studentPanel() {
        JPanel p = new JPanel(new GridLayout(2, 1, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JButton done = new JButton("Получить сертификат");
        done.addActionListener(e -> complete());
        JPanel a = new JPanel(new BorderLayout(6, 6));
        a.setBorder(BorderFactory.createTitledBorder("Мои курсы"));
        a.add(new JScrollPane(mineList), BorderLayout.CENTER);
        a.add(done, BorderLayout.SOUTH);
        inbox.setEditable(false);
        JButton refresh = new JButton("Обновить");
        refresh.addActionListener(e -> refreshMine());
        JPanel b = new JPanel(new BorderLayout(6, 6));
        b.setBorder(BorderFactory.createTitledBorder("Сообщения"));
        b.add(new JScrollPane(inbox), BorderLayout.CENTER);
        b.add(refresh, BorderLayout.SOUTH);
        p.add(a); p.add(b);
        return p;
    }

    private JScrollPane teacherPanel() {
        JPanel create = new JPanel(new GridLayout(0, 2, 6, 6));
        create.setBorder(BorderFactory.createTitledBorder("1. Создать курс"));
        Object[][] rows = {{"Название курса", tTitle}, {"Преподаватель", tTeacher}, {"Цена, $", tPrice}, {"Часов", tHours},
                {"Теги через запятую", tTags}, {"О чём курс", tDesc}, {"О преподавателе", tBio},
                {"Тип первого урока", type}, {"Название урока", lTitle}, {"Содержание урока", lData}};
        for (Object[] r : rows) { create.add(new JLabel((String) r[0])); create.add((Component) r[1]); }
        JButton createBtn = new JButton("Создать курс");
        createBtn.addActionListener(e -> createCourse());
        create.add(new JLabel()); create.add(createBtn);

        JPanel announce = new JPanel(new FlowLayout(FlowLayout.LEFT));
        announce.setBorder(BorderFactory.createTitledBorder("2. Написать студентам курса"));
        JButton send = new JButton("Отправить");
        send.addActionListener(e -> announce());
        announce.add(announceCourse); announce.add(announceText); announce.add(send);

        JPanel grade = new JPanel(new FlowLayout(FlowLayout.LEFT));
        grade.setBorder(BorderFactory.createTitledBorder("3. Поставить оценку"));
        JButton gradeBtn = new JButton("Поставить");
        gradeBtn.addActionListener(e -> grade());
        grade.add(gradeCourse); grade.add(new JLabel("Студент:")); grade.add(gradeStudent);
        grade.add(new JLabel("Оценка:")); grade.add(gradeValue); grade.add(gradeBtn);

        teacherInbox.setEditable(false);
        JScrollPane msgs = new JScrollPane(teacherInbox);
        msgs.setBorder(BorderFactory.createTitledBorder("Сообщения преподавателя"));
        msgs.setPreferredSize(new Dimension(400, 140));

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        p.add(create); p.add(announce); p.add(grade); p.add(msgs);
        return new JScrollPane(p);
    }

    // ---------------- actions ----------------
    private void msg(String text) { JOptionPane.showMessageDialog(this, text); }
    private String student() { return who.getText().trim(); }
    private Course selected() {
        int r = table.getSelectedRow();
        return r < 0 || r >= shown.size() ? null : shown.get(r);
    }
    private Server.Info infoOf(Course c) { return Server.info.getOrDefault(c.getTitle(), new Server.Info("", 0, "")); }

    private ICourseComponent offer(Course c) {
        ICourseComponent o = new BaseCourse(c);
        if (cert.isSelected()) o = new CertificatedCourseDecorator(o);
        if (mentor.isSelected()) o = new MentorshipDecorator(o);
        return o;
    }

    private void refreshAll() { refreshCourses(); refreshMine(); refreshTeacher(); }

    private void refreshCourses() {
        List<String> in = Arrays.stream(interests.getText().split(",")).map(String::trim)
                .filter(s -> !s.isBlank()).collect(Collectors.toList());
        IRecommendationStrategy strategy = new RecommendationSelector().selectStrategy(in);
        strategyLabel.setText("  Рекомендации: " + (in.isEmpty() ? "самые популярные" : "по вашим интересам"));
        List<Course> list = new RecommendationContext(strategy)
                .recommendCourses(new ArrayList<>(Server.courses.values()), Server.students, in);
        String q = search.getText().trim().toLowerCase();
        list.removeIf(c -> !(c.getTitle() + " " + c.getTeacher()).toLowerCase().contains(q));
        shown = list;
        model.setRowCount(0);
        for (Course c : list) {
            model.addRow(new Object[]{c.getTitle(), c.getTeacher(), c.getPrice(), infoOf(c).hours(),
                    Server.students.getOrDefault(c.getTitle(), 0)});
        }
        if (list.isEmpty()) updateDetails(); else table.setRowSelectionInterval(0, 0);
    }

    private void updateDetails() {
        Course c = selected();
        if (c == null) {
            details.setText("Выберите курс в таблице слева.");
            total.setText("Итого: —");
            return;
        }
        Server.Info i = infoOf(c);
        StringBuilder sb = new StringBuilder(c.getTitle()).append("\n\n").append(i.description())
                .append("\n\nПреподаватель: ").append(c.getTeacher()).append("\n").append(i.bio())
                .append("\n\nДлительность: ").append(i.hours()).append(" ч")
                .append("\nТеги: ").append(String.join(", ", c.getTags())).append("\n\nПрограмма курса:\n");
        for (Module m : c.getModules()) {
            sb.append(" • ").append(m.getTitle()).append("\n");
            for (EducationalContent x : m.getMaterials()) sb.append("      - ").append(x.getType()).append(": ").append(x.getTitle()).append("\n");
            for (Assignment a : m.getAssignments()) sb.append("      - Задание: ").append(a.getTitle()).append("\n");
        }
        details.setText(sb.toString());
        details.setCaretPosition(0);
        total.setText("Итого: $" + offer(c).getPrice());
    }

    private void buy() {
        Course c = selected();
        String st = student();
        if (c == null) { msg("Выберите курс в таблице."); return; }
        if (st.isEmpty()) { msg("Введите ваше имя."); return; }
        ICourseComponent o = offer(c);
        if (!Server.facade.purchaseCourse(st, o)) { msg("Оплата не прошла."); return; }
        Map<String, ICourseComponent> mine = Server.enrolled.computeIfAbsent(st, k -> new LinkedHashMap<>());
        boolean first = !mine.containsKey(c.getTitle());
        mine.put(c.getTitle(), o);
        CourseSubject sub = Server.subjects.get(c.getTitle());
        if (first) {
            Server.students.merge(c.getTitle(), 1, Integer::sum);
            sub.attach(new StudentNotifier(st, Server.inbox));
        }
        sub.notifyObservers(st + " enrolled in the course");
        msg("Готово! Вы записаны на курс:\n" + o.getDescription() + "\nСумма: $" + o.getPrice());
        refreshAll();
    }

    private void refreshMine() {
        String st = student();
        Map<String, ICourseComponent> m = Server.enrolled.getOrDefault(st, new LinkedHashMap<>());
        mineKeys = new ArrayList<>(m.keySet());
        mineModel.clear();
        for (Map.Entry<String, ICourseComponent> e : m.entrySet()) {
            mineModel.addElement(e.getValue().getDescription() + "   ($" + e.getValue().getPrice() + ")");
        }
        inbox.setText(messages(st));
    }

    private String messages(String user) {
        String prefix = user + "|";
        return Server.inbox.stream().filter(x -> x.startsWith(prefix))
                .map(x -> x.substring(prefix.length())).collect(Collectors.joining("\n"));
    }

    private void complete() {
        int i = mineList.getSelectedIndex();
        if (i < 0) { msg("Выберите курс в списке."); return; }
        String st = student(), title = mineKeys.get(i);
        ICourseComponent o = Server.enrolled.get(st).get(title);
        if (!o.getDescription().contains("Certificate")) { msg("Этот курс куплен без сертификата."); return; }
        String file = Server.facade.completeCourse(st, o);
        Server.subjects.get(title).notifyObservers(st + " received certificate " + file);
        msg("Сертификат выдан: " + file);
        refreshMine();
    }

    private void refreshTeacher() {
        Object a = announceCourse.getSelectedItem(), g = gradeCourse.getSelectedItem();
        announceCourse.removeAllItems(); gradeCourse.removeAllItems();
        for (String t : Server.courses.keySet()) { announceCourse.addItem(t); gradeCourse.addItem(t); }
        if (a != null) announceCourse.setSelectedItem(a);
        if (g != null) gradeCourse.setSelectedItem(g);
        teacherInbox.setText(messages(tTeacher.getText().trim()));
    }

    private void createCourse() {
        try {
            ContentFactory f = ContentFactory.forType((String) type.getSelectedItem());
            StandardCourseBuilder b = new StandardCourseBuilder().setTitle(tTitle.getText().trim())
                    .setTeacher(tTeacher.getText().trim()).setPrice(Integer.parseInt(tPrice.getText().trim()));
            for (String t : tTags.getText().split(",")) if (!t.isBlank()) b.addTag(t.trim());
            Module m = new Module("Модуль 1").addContent(f.createContent(lTitle.getText(), lData.getText()));
            Course c = b.addModule(m).build();
            Server.info.put(c.getTitle(), new Server.Info(tDesc.getText(), Integer.parseInt(tHours.getText().trim()), tBio.getText()));
            Server.publish(c);
            msg("Курс создан! Он появился на вкладке «Курсы».");
            refreshAll();
        } catch (Exception e) {
            msg("Не удалось создать курс: " + e.getMessage());
        }
    }

    private void announce() {
        String course = (String) announceCourse.getSelectedItem();
        if (course == null || announceText.getText().isBlank()) { msg("Выберите курс и введите сообщение."); return; }
        Server.subjects.get(course).publishAnnouncement(announceText.getText().trim());
        announceText.setText("");
        msg("Сообщение отправлено студентам.");
        refreshAll();
    }

    private void grade() {
        String course = (String) gradeCourse.getSelectedItem(), st = gradeStudent.getText().trim();
        if (course == null || !Server.enrolled.getOrDefault(st, new LinkedHashMap<>()).containsKey(course)) {
            msg("Этот студент не записан на выбранный курс.");
            return;
        }
        try {
            Server.subjects.get(course).gradeAssignment(st, (Integer) gradeValue.getValue());
            msg("Оценка отправлена студенту.");
            refreshAll();
        } catch (IllegalArgumentException e) {
            msg(e.getMessage());
        }
    }
}
