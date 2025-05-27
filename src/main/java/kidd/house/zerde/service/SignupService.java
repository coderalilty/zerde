package kidd.house.zerde.service;

import kidd.house.zerde.dto.signupLesson.FreeLesson;
import kidd.house.zerde.dto.signupLesson.LessonTypeDto;
import kidd.house.zerde.dto.signupLesson.SignUpLessonResponse;
import kidd.house.zerde.dto.signupLesson.SignupRequestDto;
import kidd.house.zerde.model.entity.Child;
import kidd.house.zerde.model.entity.Lesson;
import kidd.house.zerde.model.entity.Parent;
import kidd.house.zerde.model.entity.Room;
import kidd.house.zerde.model.record.LessonDay;
import kidd.house.zerde.model.status.LessonStatus;
import kidd.house.zerde.model.type.GroupType;
import kidd.house.zerde.model.type.LessonType;
import kidd.house.zerde.repo.LessonRepo;
import kidd.house.zerde.repo.LockedSlotRepo;
import kidd.house.zerde.repo.ParentRepo;
import kidd.house.zerde.repo.RoomRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
public class SignupService {
    @Autowired
    private LessonRepo lessonRepo;
    @Autowired
    private ParentRepo parentRepo;
    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private MailSenderService mailSenderService;  // Сервис для отправки email
    @Autowired
    private LockedSlotRepo lockedSlotRepo;
    public String saveSignup(SignupRequestDto signupRequest, String status) {
        // Валидация запроса
        if (signupRequest == null) {
            log.error("Пустой запрос на запись урока");
            throw new IllegalArgumentException("Запрос не может быть пустым.");
        }

        if (signupRequest.parentName() == null || signupRequest.parentPhone() == null || signupRequest.parentEmail() == null) {
            log.warn("Данные родителя неполные: {}", signupRequest);
            throw new IllegalArgumentException("Пожалуйста, укажите данные родителя.");
        }

        if (signupRequest.childName() == null || signupRequest.age() == 0) {
            log.warn("Данные ребенка неполные: {}", signupRequest);
            throw new IllegalArgumentException("Пожалуйста, укажите данные ребенка.");
        }
        Parent parent = new Parent();
        parent.setParentName(signupRequest.parentName());
        parent.setParentPhone(signupRequest.parentPhone());
        parent.setParentEmail(signupRequest.parentEmail());


        Child child = new Child();
        child.setFirstName(signupRequest.childName());
        child.setAge(signupRequest.age());
        //Не нужен потому что cascade = CascadeType.ALL указан в model
        //childRepo.save(child);
        Room room = new Room();
        room.setName("202");

        Lesson lesson = new Lesson();
        lesson.setLessonName(signupRequest.lessonTypeDto().lessonName());
        // !!! Копируем freeLessons в изменяемый список
        List<FreeLesson> freeLessons = new ArrayList<>(signupRequest.lessonTypeDto().freeLessons());
        if (freeLessons.isEmpty()) {
            log.error("Выберите свободное время!");
            throw new IllegalArgumentException("Свободное время для урока не указано.");
        }

        lesson.setFrom(freeLessons.get(0).dateFrom());
        lesson.setTo(freeLessons.get(0).dateTo());
        lesson.setRoom(room);
        boolean isLocked = lockedSlotRepo.existsByRoomNameAndLockedFromLessThanAndLockedToGreaterThan(
                room.getName(), lesson.getTo(), lesson.getFrom()
        );

        if (isLocked) {
            throw new IllegalStateException("Время заблокировано, нельзя добавить урок");
        }

        lesson.setLessonDay(signupRequest.lessonDay());
        lesson.setLessonTime(signupRequest.lessonTime());
        lesson.setLessonType(LessonType.LOGOPED);
        lesson.setLessonStatus(LessonStatus.SCHEDULED);
        lesson.setGroupType(GroupType.GROUP);

        lesson.setParent(parent);

        child.setLesson(lesson);
        lesson.getChildren().add(child);


        // Логика сохранения записи с начальным статусом "draft"

        // Проверка, сохранился ли урок
        boolean isLessonSaved = verifySignup(signupRequest);

        if (isLessonSaved) {
            parentRepo.save(parent);
            roomRepo.save(room);
            lessonRepo.save(lesson);
            log.info("Сохранение записи с начальным статусом: " + status);
            log.info("Урок успешно сохранен.");
        } else {
            log.error("Ошибка сохранения урока.");
        }
        return status;
    }
    public boolean verifySignup(SignupRequestDto signupRequest) {
        Optional<Lesson> lessonOptional = lessonRepo.findByFromAndToAndLessonTime(
                signupRequest.lessonTypeDto().freeLessons().get(0).dateFrom(),
                signupRequest.lessonTypeDto().freeLessons().get(0).dateTo(),
                signupRequest.lessonTime()
        );

        // Возвращаем true, если урок найден, иначе false
        return lessonOptional != null;
    }

    public String updateStatus(SignupRequestDto signupRequest, String newStatus) {
        Optional<Lesson> optionalLesson = lessonRepo.findByFromAndToAndLessonTime(
                signupRequest.lessonTypeDto().freeLessons().get(0).dateFrom(),
                signupRequest.lessonTypeDto().freeLessons().get(0).dateTo(),
                signupRequest.lessonTime()
        );

        Lesson lesson = optionalLesson.orElseThrow(() ->
                new IllegalStateException("Lesson не найден для указанных параметров")
        );

        lesson.setLessonStatus(LessonStatus.RESERVED);
        lessonRepo.save(lesson);

        log.warn("Статус заявки обновлен на: " + newStatus);
        return newStatus;
    }


    public void sendNotification(SignupRequestDto signupRequest) {
        // Логика отправки уведомления (в будущем можно интегрировать WhatsApp/Telegram API)
        Parent parent = parentRepo.findByParentPhoneAndParentEmail(
                signupRequest.parentPhone(),
                signupRequest.parentEmail()
        );

        // Формирование сообщения
        String message = String.format(
                "Уважаемый(ая) %s, у вас запланирован урок с преподавателем %s, который состоится с %s до %s в комнате %s.",
                signupRequest.childName(),
                "Gregory",
                signupRequest.lessonTypeDto().freeLessons().get(0).dateFrom(),
                signupRequest.lessonTypeDto().freeLessons().get(0).dateTo(),
                "202"
        );
        // Отправка email родителю, если указан email
        if (parent.getParentEmail() != null) {
            mailSenderService.send(
                    parent.getParentEmail(),
                    "Напоминание о предстоящем уроке",
                    message
            );
        }
        System.out.println("Отправка уведомления для заявки: " + signupRequest.childName());
    }

    public List<SignUpLessonResponse> getLessons() {
        List<Lesson> lessons = lessonRepo.findAll();

        return lessons.stream()
                .collect(Collectors.groupingBy(
                        lesson -> lesson.getLessonDay() + "_" + lesson.getLessonName() + "_" + lesson.getLessonTime()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<Lesson> groupedLessons = entry.getValue();
                    Lesson firstLesson = groupedLessons.get(0);

                    // Сортируем по времени начала
                    List<FreeLesson> freeLessons = groupedLessons.stream()
                            .sorted(Comparator.comparing(Lesson::getFrom))
                            .map(l -> new FreeLesson(l.getFrom(), l.getTo()))
                            .toList();

                    LessonDay lessonDay = new LessonDay(
                            firstLesson.getLessonDay(),
                            firstLesson.getLessonTime()
                    );

                    LessonTypeDto lessonTypeDto = new LessonTypeDto(
                            firstLesson.getLessonName(),
                            freeLessons
                    );

                    return new SignUpLessonResponse(lessonDay, lessonTypeDto);
                })
                .sorted(Comparator.comparing(r -> r.lessonDay().weekDay())) // Сортировка по дню
                .toList();
    }

}
