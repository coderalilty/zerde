package kidd.house.zerde.controller;

import kidd.house.zerde.dto.adminDto.*;
import kidd.house.zerde.dto.lockLesson.LockLessonRequest;
import kidd.house.zerde.dto.schedule.ChildDto;
import kidd.house.zerde.dto.schedule.LessonDto;
import kidd.house.zerde.dto.schedule.RoomDto;
import kidd.house.zerde.dto.sendNotification.NotificationRequestDto;
import kidd.house.zerde.dto.weekSchedule.WeekScheduleResponse;
import kidd.house.zerde.mapper.LessonMapper;
import kidd.house.zerde.model.entity.Lesson;
import kidd.house.zerde.service.AdminService;
import kidd.house.zerde.service.LessonService;
import kidd.house.zerde.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final LessonService lessonService;  // Сервис для работы с уроками
    private final LessonMapper lessonMapper;
    private final MailSenderService mailSenderService;
    private final AdminService adminService;
    @GetMapping("/first-visit-schedule")
    public ResponseEntity<List<LessonDto>> schedule(){
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
        String lessonTime = lessonMapper.getLessonTime();
        // Группируем уроки по комнатам
        Map<String, List<LessonDto>> roomMap = lessons.stream()
                .collect(Collectors.groupingBy(lesson -> lesson.roomDto().name()));

        // Формируем список объектов WeekScheduleResponse
        List<WeekScheduleResponse> weekSchedule = new ArrayList<>();
        roomMap.forEach((roomName, lessonList) -> {
            RoomDto room = new RoomDto(roomName);
            weekSchedule.add(new WeekScheduleResponse(
                    //LocalDate.now().format(formatter)
                    lessonTime, // Дата
                    room, // Комната
                    lessonList // Уроки в комнате
            ));
        });

        return ResponseEntity.ok(weekSchedule);
    }

    @GetMapping("/lessons")
    public ResponseEntity<List<LessonDtos>> getAllLessons(){
        List<LessonDtos> lessons = adminService.getLessons();
       return ResponseEntity.ok(lessons);
    }
    @GetMapping("/lessons/{lessonId}/children")
    public ResponseEntity<List<ChildDtos>> getChildList(@PathVariable Integer lessonId){
        List<ChildDtos> children = adminService.getChildrenByLessonId(lessonId);
        return ResponseEntity.ok(children);
    }
    @GetMapping("/children")
    public ResponseEntity<List<ChildDtos>> getChildList(){
        List<ChildDtos> childDtosList = adminService.getChildren();
        return ResponseEntity.ok(childDtosList);
    }
    @GetMapping("/rooms")
    public ResponseEntity<List<ListRoomsDto>> getRooms(){
        List<ListRoomsDto> rooms = adminService.getRooms();
        return ResponseEntity.ok(rooms);
    }
    @GetMapping("/teachers")
    public ResponseEntity<List<ListTeachersDto>> getTeachers(){
        List<ListTeachersDto> teachers = adminService.getTeachers();
        return ResponseEntity.ok(teachers);
    }
    @GetMapping("/subjects")
    public ResponseEntity<List<ListSubjectsDto>> getSubjects(){
        List<ListSubjectsDto> subjects = adminService.getSubjects();
        return ResponseEntity.ok(subjects);
    }
    @GetMapping("/groups")
    public ResponseEntity<List<ListGroupsDto>> getGroups(){
        List<ListGroupsDto> groups = adminService.getGroups();
        return ResponseEntity.ok(groups);
    }
    @GetMapping("/lock-lesson")
    public ResponseEntity<List<LockLessonDto>> getLockLesson(){
        List<LockLessonDto> lockLesson = lessonService.getLockLesson();
        return  ResponseEntity.ok(lockLesson);
    }
    @GetMapping("/profile")
    public ResponseEntity<List<AdminProfileDto>> getAdminProfiles(){
        List<AdminProfileDto> adminProfileDto = adminService.getAdminProfiles();
        return ResponseEntity.ok(adminProfileDto);
    }
    @PostMapping("/create-teacher")
    public ResponseEntity<String> createTeacher(@RequestBody CreateTeacherDto createTeacherDto){
        adminService.createNewTeacher(createTeacherDto);
        return new ResponseEntity<>("Teacher successfully created!",HttpStatus.CREATED);
    }
    @PostMapping("/create-subject")
    public ResponseEntity<String> createSubject(@RequestBody CreateSubjectDto createSubjectDto){
        adminService.createNewSubject(createSubjectDto);
        return new ResponseEntity<>("Subject successfully created!",HttpStatus.CREATED);
    }
    @PostMapping("/create-room")
    public ResponseEntity<String> createRoom(@RequestBody CreateRoomDto createRoomDto){
        adminService.createNewRoom(createRoomDto);
        return new ResponseEntity<>("Room successfully created!",HttpStatus.CREATED);
    }
    @PostMapping("/create-group")
    public ResponseEntity<String> createGroup(@RequestBody CreateGroupDto createGroupDto){
        adminService.createNewGroup(createGroupDto);
        return new ResponseEntity<>("Group successfully created!",HttpStatus.CREATED);
    }
    @PostMapping("/create-lesson")
    public ResponseEntity<String> createLesson(@RequestBody CreateLessonDto createLessonDto){
        adminService.createNewLesson(createLessonDto);
        adminService.sendNotification(createLessonDto);
        return new ResponseEntity<>("Lesson successfully created!",HttpStatus.CREATED);
    }
    @PostMapping("/create-child")
    public ResponseEntity<String> createChild(@RequestBody ChildDtos childDtos){
        adminService.createNewChild(childDtos);
        return new ResponseEntity<>("Child successfully created!",HttpStatus.CREATED);
    }
    @PostMapping("/lock-lesson")
    public ResponseEntity<String> lockLesson(@RequestBody LockLessonRequest lockLessonRequest) {

        String lockDateTimeFrom = lockLessonRequest.lockDateTimeFrom();
        String lockDateTimeTo = lockLessonRequest.lockDateTimeTo();
        String roomName = lockLessonRequest.roomName();
        // 1. Проверка: есть ли уроки в указанное время
        lessonService.lockLesson(lockDateTimeFrom, lockDateTimeTo, roomName);

        return new ResponseEntity<>("Lesson locked successfully for room ID " + roomName
                + " from " + lockDateTimeFrom + " to " + lockDateTimeTo, HttpStatus.OK);
    }

    @PostMapping("/send-notification")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequestDto notificationRequest) {
        int lessonId = notificationRequest.lessonId();
        Lesson lesson = lessonService.findById(lessonId);
        // Поиск урока по lessonId через сервис
        if (lesson != null) {
            return ResponseEntity.status(404).body("Lesson not found");
        }

        List<ChildDto> childFirstName = lessonMapper.getChildFirstName(lesson);

        // Формирование сообщения
        String message = String.format(
                "Уважаемый(ая) %s, у вас запланирован урок с преподавателем %s, который состоится с %s до %s в комнате %s.",
                childFirstName,
                "Gregory",
                lesson.getFrom(),
                lesson.getTo(),
                "202"
        );
        try {
            // Отправка email родителю, если указан email
            if (lesson.getChildren().get(0).getParent().getParentEmail() != null) {
                mailSenderService.send(
                        lesson.getChildren().get(0).getParent().getParentEmail(),
                        "Напоминание о предстоящем уроке",
                        message
                );
            }
            System.out.println("Формируемое сообщение: " + message);
            // Отправка уведомления в Telegram, если указан номер телефона
//            if (lesson.get().getParent().getParentPhone() != null) {
//                String chatId = parentService.getChatId(1L);
//                System.out.println("Полученный chatId: " + chatId);
//                if (chatId != null) {
//                    telegramService.sendMessageToChat(Long.valueOf(chatId),message);
//                } else {
//                    System.out.println("chatId не найден для телефона: " + lesson.get().getParent().getParentPhone());
//                }
//            } else {
//                System.out.println("Номер телефона родителя не указан.");
//            }

        } catch (Exception e) {
            // Логгирование ошибки
            System.err.println("Ошибка при отправке уведомления: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to send notification");
        }

        return ResponseEntity.ok("Notification for lesson ID " + lessonId + " sent successfully.");
    }
    @PutMapping("/edit_lesson/{lesson_id}")
    public ResponseEntity<String> editLesson(@PathVariable int lesson_id,@RequestBody LessonDtos lessonDtos){
        lessonService.editLesson(lesson_id,lessonDtos);
        return new ResponseEntity<>("Lesson edited",HttpStatus.OK);
    }
    @PutMapping("/edit_child/{child_id}")
    public ResponseEntity<String> editChild(@PathVariable int child_id,@RequestBody ChildDtos childDtos){
        adminService.editChild(child_id,childDtos);
        return new ResponseEntity<>("Child edited",HttpStatus.OK);
    }
    @PutMapping("/edit_teacher/{teacher_id}")
    public ResponseEntity<String> editTeacher(@PathVariable int teacher_id, @RequestBody CreateTeacherDto createTeacherDto){
        adminService.editTeacher(teacher_id,createTeacherDto);
        return new ResponseEntity<>("Teacher edited",HttpStatus.OK);
    }
    @PutMapping("/edit-subject/{subject_id}")
    public ResponseEntity<String> editSubject(@PathVariable int subject_id,@RequestBody CreateSubjectDto createSubjectDto){
        adminService.editSubject(subject_id,createSubjectDto);
        return new ResponseEntity<>("Subject edited",HttpStatus.OK);
    }
    @PutMapping("/edit-room/{room_id}")
    public ResponseEntity<String> editRoom(@PathVariable int room_id,@RequestBody CreateRoomDto createRoomDto){
        adminService.editRoom(room_id,createRoomDto);
        return new ResponseEntity<>("Room edited",HttpStatus.OK);
    }
    @PutMapping("/edit-group/{app_group_id}")
    public ResponseEntity<String> editGroup(@PathVariable int app_group_id,@RequestBody CreateGroupDto createGroupDto){
        adminService.editGroup(app_group_id,createGroupDto);
        return new ResponseEntity<>("Room edited",HttpStatus.OK);
    }
    @DeleteMapping("/lock-lesson/{lockLesson_id}")
    public ResponseEntity<String> deleteLockLesson(@PathVariable int lockLesson_id){
        lessonService.deleteLockLesson(lockLesson_id);
        return new ResponseEntity<>("DeleteLockLesson success",HttpStatus.OK);
    }
    @DeleteMapping("/delete-lesson/{lesson_id}")
    public ResponseEntity<String> deleteLesson(@PathVariable int lesson_id){
        lessonService.deleteLesson(lesson_id);
        return new ResponseEntity<>("Delete Lesson success",HttpStatus.OK);
    }
    @DeleteMapping("/delete-child/{child_id}")
    public ResponseEntity<String> deleteChild(@PathVariable int child_id){
        adminService.deleteChild(child_id);
        return new ResponseEntity<>("Delete Child success",HttpStatus.OK);
    }
    @DeleteMapping("/delete-teacher/{teacher_id}")
    public ResponseEntity<String> deleteTeacher(@PathVariable int teacher_id){
        adminService.deleteTeacher(teacher_id);
        return new ResponseEntity<>("Delete Teacher success",HttpStatus.OK);
    }
    @DeleteMapping("/delete-group/{app_group_id}")
    public ResponseEntity<String> deleteGroup(@PathVariable int app_group_id){
        adminService.deleteGroup(app_group_id);
        return new ResponseEntity<>("Delete Teacher success",HttpStatus.OK);
    }
}
