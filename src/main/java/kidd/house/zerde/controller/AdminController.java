package kidd.house.zerde.controller;

import kidd.house.zerde.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    @GetMapping
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hi Admin");
    }
    @GetMapping("/first-visit-schedule")
    public ResponseEntity<List<LessonDto>> schedule(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<LessonDto> lessons = new ArrayList<>();
        lessons.add(new LessonDto(
                1,
                new TeacherDto("Грегориевна Анна Александровна"),
                new ChildDto("Петров Петр Петрович"),
                LocalDateTime.of(2024, 10, 13, 10, 0).format(formatter), // Дата и время начала
                LocalDateTime.of(2024, 10, 13, 11, 0).format(formatter), // Дата и время окончания
                new RoomDto( "Кабинет 101"),
                new ParentDto("Касандра Александровна","+77754768093")
        ));
        return ResponseEntity.ok(lessons);
    }
}
