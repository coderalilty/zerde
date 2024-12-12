package kidd.house.zerde.service;

import kidd.house.zerde.dto.schedule.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final List<LessonDto> lessons;
    public LessonService() {
        // Инициализация уроков (пример)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.lessons = new ArrayList<>();
        lessons.add(new LessonDto(
                1,
                new TeacherDto("Грегориевна Анна Александровна"),
                new ChildDto("Петров Петр Петрович"),
                LocalDateTime.of(2024, 10, 13, 10, 0).format(formatter),
                LocalDateTime.of(2024, 10, 13, 11, 0).format(formatter),
                new RoomDto("Кабинет 101"),
                new ParentDto("Касандра Александровна", "+77754768093", "elzat.sayatov.3@gmail.com")
        ));
    }
    public List<LessonDto> getAllLessons() {
        return lessons;
    }
    public LessonDto findById(int lessonId) {
        lessons.forEach(lesson -> System.out.println("Урок ID: " + lesson.lessonId()));
        return lessons.stream()
                .filter(lesson -> lesson.lessonId() == lessonId)
                .findFirst()
                .orElse(null);
    }

}
