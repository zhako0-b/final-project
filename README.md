# Online Education Platform

Командный итоговый проект по курсу Software Design Patterns (Java).
Платформа онлайн-обучения, в которой 6 паттернов проектирования работают вместе в одном сценарии.

## Паттерны и распределение

| Студент | Паттерны | Пакеты |
|---------|----------|--------|
| Студент 1 | Factory Method, Builder | `model`, `factory`, `builder` |
| Студент 2 | Strategy, Observer | `strategy`, `observer` |
| Студент 3 | Decorator, Facade (+ интеграция) | `decorator`, `facade`, `Main.java` |

## Структура проекта

```
com.education.platform
├── Main.java                  # сквозной демо-сценарий
├── model/                     # Course, Module, Assignment
├── factory/                   # EducationalContent, Video/Text/QuizContent, ContentFactory
├── builder/                   # ICourseBuilder, StandardCourseBuilder
├── decorator/                 # ICourseComponent, BaseCourse, CourseDecorator,
│                              # CertificatedCourseDecorator, MentorshipDecorator
├── strategy/                  # IRecommendationStrategy, PopularityStrategy,
│                              # TagMatchStrategy, RecommendationContext
├── observer/                  # IObserver, ISubject, CourseSubject,
│                              # StudentNotifier, TeacherNotifier
└── facade/                    # PaymentSubSystem, CertificateGenerator, PlatformFacade
```

## Как паттерны применяются

- **Factory Method** (`ContentFactory`): создаёт `VideoContent`, `TextContent`, `QuizContent` по строковому типу, без прямого `new` в клиентском коде.
- **Builder** (`StandardCourseBuilder`): пошаговая сборка `Course` (название, преподаватель, цена, теги, контент).
- **Decorator** (`CertificatedCourseDecorator`, `MentorshipDecorator`): динамически добавляют опции к курсу и пересчитывают цену. Новую опцию можно добавить без изменения существующих классов (OCP).
- **Strategy** (`RecommendationContext`): алгоритм рекомендаций (по популярности или по тегам) меняется во время работы программы, без громоздких `if/else`.
- **Observer** (`CourseSubject`): уведомляет студентов и преподавателей о событиях курса (сдача ДЗ, анонсы), связи между курсом и подписчиками слабые.
- **Facade** (`PlatformFacade`): один метод скрывает оплату, зачисление на курс и генерацию сертификата.

## Сквозной сценарий (`Main.java`)

Factory → Builder → Decorator → Strategy → Observer → Facade:

1. Фабрика создаёт видео, текст и квиз.
2. Builder собирает курс из этого контента.
3. Decorator добавляет сертификат и менторство, цена пересчитывается.
4. Strategy выдаёт рекомендации, стратегия переключается на лету.
5. Observer рассылает уведомления студенту и преподавателю.
6. Facade проводит оплату, зачисление и выдачу сертификата.

## Сборка и запуск

Требуется JDK 11+.

```bash
# из корня проекта (папка, содержащая com/)
javac -d out $(find . -name "*.java")
java -cp out com.education.platform.Main
```

## Команда

- Студент 1: _имя_
- Студент 2: _имя_
- Студент 3: _имя_

## Отчёт

PDF-отчёт (введение, основная часть, заключение, Further work, код, скриншоты, UML-диаграммы) сдаётся в Moodle вместе с zip-архивом исходного кода.
