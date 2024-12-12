package kidd.house.zerde.controller;

import kidd.house.zerde.dto.schedule.*;
import kidd.house.zerde.dto.weekSchedule.WeekScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @GetMapping("/week-schedule")
    public ResponseEntity<List<WeekScheduleResponse>> weekSchedule() {
        // Получаем уроки из метода schedule()
        List<LessonDto> lessons = schedule().getBody();

        // Формат для дат
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Группируем уроки по комнатам
        Map<String, List<LessonDto>> roomMap = lessons.stream()
                .collect(Collectors.groupingBy(lesson -> lesson.roomDto().name()));

        // Формируем список объектов WeekScheduleResponse
        List<WeekScheduleResponse> weekSchedule = new ArrayList<>();
        roomMap.forEach((roomName, lessonList) -> {
            RoomDto room = new RoomDto(roomName);
            weekSchedule.add(new WeekScheduleResponse(
                    LocalDate.now().format(formatter), // Дата
                    List.of(room), // Комната
                    lessonList // Уроки в комнате
            ));
        });

        return ResponseEntity.ok(weekSchedule);
    }
}
