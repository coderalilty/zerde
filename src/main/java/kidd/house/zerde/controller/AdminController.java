package kidd.house.zerde.controller;

import kidd.house.zerde.dto.lockLesson.LockLessonRequest;
import kidd.house.zerde.dto.schedule.*;
import kidd.house.zerde.dto.sendNotification.NotificationRequestDto;
import kidd.house.zerde.dto.weekSchedule.WeekScheduleResponse;
import kidd.house.zerde.service.LessonService;
import kidd.house.zerde.service.MailSenderService;
import kidd.house.zerde.service.ParentService;
import kidd.house.zerde.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final LessonService lessonService;  // Сервис для работы с уроками
    private final MailSenderService mailSenderService;  // Сервис для отправки email
    private final TelegramService telegramService;
    private final ParentService parentService;
    @GetMapping
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hi Admin");
    }
    @GetMapping("/first-visit-schedule")
    public ResponseEntity<List<LessonDto>> schedule(){
//        // Получаем список уроков из сервиса
//        List<LessonDto> lessons = lessonService.getAllLessons();
//        return ResponseEntity.ok(lessons);
        // Получаем список уроков из сервиса
        List<LessonDto> lessons = lessonService.getAllLessons();
        if (!lessons.isEmpty()){
            return new ResponseEntity<>(lessons, HttpStatus.OK);
        }
        return new ResponseEntity<>(null , HttpStatus.NOT_FOUND);
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
    @PostMapping("/lock-lesson")
    public ResponseEntity<String> lockLesson(@RequestBody LockLessonRequest lockLessonRequest) {
        // Логика для поиска свободного урока и блокировки

        // Примерная логика:
        // 1. Поиск свободного урока по roomId (можно проверить по базе данных)
        // 2. Проверка доступности комнаты на указанный интервал времени (lockDateTimeFrom, lockDateTimeTo)
        // 3. Если комната свободна, устанавливаем заглушку и сохраняем изменения

        String lockDateTimeFrom = lockLessonRequest.lockDateTimeFrom();
        String lockDateTimeTo = lockLessonRequest.lockDateTimeTo();
        int roomId = lockLessonRequest.roomId();

        // Логика проверки свободного статуса и блокировки (заглушки)
        // Например, через вызов соответствующего сервиса:
        // lessonService.lockLesson(lockDateTimeFrom, lockDateTimeTo, roomId);

        return ResponseEntity.ok("Lesson locked successfully for room ID " + roomId
                + " from " + lockDateTimeFrom + " to " + lockDateTimeTo);
    }
    @PostMapping("/send-notification")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequestDto notificationRequest) {
        int lessonId = notificationRequest.lessonId();
        LessonDto lesson = lessonService.findById(lessonId);
        // Поиск урока по lessonId через сервис
        if (lesson == null) {
            return ResponseEntity.status(404).body("Lesson not found");
        }

        // Формирование сообщения
        String message = String.format(
                "Уважаемый(ая) %s, у вас запланирован урок с преподавателем %s, который состоится с %s до %s в комнате %s.",
                lesson.childDto().child(),
                lesson.teacherName().name(),
                lesson.from(),
                lesson.to(),
                lesson.roomDto().name()
        );
        try {
            // Отправка email родителю, если указан email
            if (lesson.parentDto().email() != null) {
                mailSenderService.send(
                        lesson.parentDto().email(),
                        "Напоминание о предстоящем уроке",
                        message
                );
            }
            System.out.println("Формируемое сообщение: " + message);
            // Отправка уведомления в Telegram, если указан номер телефона
            if (lesson.parentDto().phoneNumber() != null) {
                String chatId = parentService.getChatId(1L);
                System.out.println("Полученный chatId: " + chatId);
                if (chatId != null) {
                    telegramService.sendMessageToChat(Long.valueOf(chatId),message);
                } else {
                    System.out.println("chatId не найден для телефона: " + lesson.parentDto().phoneNumber());
                }
            } else {
                System.out.println("Номер телефона родителя не указан.");
            }

        } catch (Exception e) {
            // Логгирование ошибки
            System.err.println("Ошибка при отправке уведомления: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to send notification");
        }

        return ResponseEntity.ok("Notification for lesson ID " + lessonId + " sent successfully.");
    }
}
