package kidd.house.zerde.service;

import kidd.house.zerde.dto.signupLesson.FreeLesson;
import kidd.house.zerde.dto.signupLesson.LessonTypeDto;
import kidd.house.zerde.dto.signupLesson.SignUpLessonResponse;
import kidd.house.zerde.dto.signupLesson.SignupRequestDto;
import kidd.house.zerde.model.entity.*;
import kidd.house.zerde.model.record.LessonDay;
import kidd.house.zerde.model.status.LessonStatus;
import kidd.house.zerde.model.type.LessonType;
import kidd.house.zerde.repo.*;
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
    private MailSenderService mailSenderService;
    @Autowired
    private LockedSlotRepo lockedSlotRepo;
    @Autowired
    private LessonService lessonService;

    public String saveSignup(SignupRequestDto signupRequest, String status) {
        // === 1. Валидация ===
        if (signupRequest == null) {
            log.error("Пустой запрос на запись урока");
            throw new IllegalArgumentException("Запрос не может быть пустым.");
        }

        if (signupRequest.parentName() == null || signupRequest.parentPhone() == null || signupRequest.parentEmail() == null) {
            log.warn("Данные родителя неполные: {}", signupRequest);
            throw new IllegalArgumentException("Пожалуйста, укажите данные родителя.");
        }

        if (signupRequest.childName() == null || signupRequest.childAge() == 0) {
            log.warn("Данные ребенка неполные: {}", signupRequest);
            throw new IllegalArgumentException("Пожалуйста, укажите данные ребенка.");
        }

        // === 2. Создание сущностей ===
        Parent parent = new Parent();
        parent.setParentName(signupRequest.parentName());
        parent.setParentPhone(signupRequest.parentPhone());
        parent.setParentEmail(signupRequest.parentEmail());

        Child child = new Child();
        child.setFirstName(signupRequest.childName());
        child.setAge(signupRequest.childAge());
        child.setParent(parent);

        Room room = new Room();
        room.setName("202");

        Lesson lesson = new Lesson();
        lesson.setLessonName(signupRequest.lessonTypeDto().lessonName());

        List<FreeLesson> freeLessons = new ArrayList<>(signupRequest.lessonTypeDto().freeLessons());
        if (freeLessons.isEmpty()) {
            log.error("Выберите свободное время!");
            throw new IllegalArgumentException("Свободное время для урока не указано.");
        }

        lesson.setFrom(freeLessons.get(0).createTimeFrom());
        lesson.setTo(freeLessons.get(0).createTimeTo());
        lesson.setRoom(room);
        lesson.setLessonDay(signupRequest.lessonDay());
        lesson.setLessonType(LessonType.LOGOPED);
        lesson.setLessonStatus(LessonStatus.SCHEDULED);
        lesson.setGroupType(signupRequest.lessonTypeDto().groupType());

        child.setLesson(lesson);
        lesson.getChildren().add(child);

        // === 3. Проверка, свободно ли время ===
        if (signupRequest.lessonTypeDto().groupType().equals("GROUP")){
            // Проверка: нет ли уже заглушки
            List<LockedSlot> lockedSlots = lockedSlotRepo
                    .findLockedBetween(
                            lesson.getFrom(),
                            lesson.getTo(),
                            lesson.getRoom().getName()
                    );

            if (!lockedSlots.isEmpty()) {
                throw new IllegalStateException("Уже стоит индеведуальный урок на это время!");
            }
        } else if (signupRequest.lessonTypeDto().groupType().equals("INDIVIDUAL")) {
            lessonService.lockLesson(lesson.getFrom(), lesson.getTo(), room.getName());
        }


        // === 4. Сохранение сущностей ===
        parentRepo.save(parent);
        roomRepo.save(room);
        lessonRepo.save(lesson);

        log.info("Сохранение записи с начальным статусом: {}", status);
        log.info("Урок успешно сохранен.");

        return status;
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
                signupRequest.lessonTypeDto().freeLessons().get(0).createTimeFrom(),
                signupRequest.lessonTypeDto().freeLessons().get(0).createTimeTo(),
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
                        lesson -> lesson.getLessonDay() + "_" + lesson.getLessonName()
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
                            firstLesson.getLessonDay()
                    );

                    LessonTypeDto lessonTypeDto = new LessonTypeDto(
                            firstLesson.getLessonName(),
                            "GROUP",
                            freeLessons
                    );

                    return new SignUpLessonResponse(lessonDay, lessonTypeDto);
                })
                .sorted(Comparator.comparing(r -> r.lessonDay().weekDay())) // Сортировка по дню
                .toList();
    }

}
