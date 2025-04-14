package kidd.house.zerde.service;

import kidd.house.zerde.dto.signupLesson.FreeLesson;
import kidd.house.zerde.dto.signupLesson.LessonTypeDto;
import kidd.house.zerde.dto.signupLesson.SignUpLessonResponse;
import kidd.house.zerde.dto.signupLesson.SignupRequestDto;
import kidd.house.zerde.model.entity.Child;
import kidd.house.zerde.model.entity.Diagnosis;
import kidd.house.zerde.model.entity.Lesson;
import kidd.house.zerde.model.entity.Parent;
import kidd.house.zerde.model.record.LessonDay;
import kidd.house.zerde.model.status.LessonStatus;
import kidd.house.zerde.model.type.GroupType;
import kidd.house.zerde.model.type.LessonType;
import kidd.house.zerde.repo.ChildRepo;
import kidd.house.zerde.repo.DiagnosisRepo;
import kidd.house.zerde.repo.LessonRepo;
import kidd.house.zerde.repo.ParentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SignupService {
    @Autowired
    private LessonRepo lessonRepo;
    @Autowired
    private ParentRepo parentRepo;
    @Autowired
    private ChildRepo childRepo;
    @Autowired
    private DiagnosisRepo diagnosisRepo;
    @Autowired
    private MailSenderService mailSenderService;  // Сервис для отправки email
    public String saveSignup(SignupRequestDto signupRequest, String status) {
        // Логика сохранения записи с начальным статусом "draft"
        saveLesson(signupRequest);
        saveChild(signupRequest);
        saveDiagnosis(signupRequest);
        saveParent(signupRequest);
        System.out.println("Сохранение записи с начальным статусом: " + status);
        // Проверка, сохранился ли урок
        boolean isLessonSaved = verifySignup(signupRequest);

        if (isLessonSaved) {
            System.out.println("Урок успешно сохранен.");
        } else {
            System.out.println("Ошибка сохранения урока.");
        }
        return status;
    }
    public void saveLesson(SignupRequestDto signupRequest){
        Lesson lesson = new Lesson();
        lesson.setLessonName(signupRequest.lessonTypeDto().lessonName());
        lesson.setFrom(signupRequest.lessonTypeDto().freeLessons().getFirst().dateFrom());
        lesson.setTo(signupRequest.lessonTypeDto().freeLessons().getFirst().dateTo());
        lesson.setLessonDay(signupRequest.lessonDay());
        lesson.setLessonTime(signupRequest.lessonTime());
        lesson.setLessonType(LessonType.LOGOPED);
        lesson.setLessonStatus(LessonStatus.SCHEDULED);
        lesson.setGroupType(GroupType.GROUP);
        lessonRepo.save(lesson);
    }
    public void saveChild(SignupRequestDto signupRequest){
        Child child = new Child();
        child.setFirstName(signupRequest.childName());
        child.setAge(signupRequest.age());
        childRepo.save(child);
    }
    public void saveDiagnosis(SignupRequestDto signupRequest){
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setName(signupRequest.diagnosis());
        diagnosisRepo.save(diagnosis);
    }
    public void saveParent(SignupRequestDto signupRequest){
        Parent parent = new Parent();
        parent.setParentName(signupRequest.parentName());
        parent.setParentPhone(signupRequest.parentPhone());
        parent.setParentEmail(signupRequest.parentEmail());
        parentRepo.save(parent);
    }
    public boolean verifySignup(SignupRequestDto signupRequest) {
        Lesson lesson = lessonRepo.findByFromAndTo(
                signupRequest.lessonTypeDto().freeLessons().getFirst().dateFrom(),
                signupRequest.lessonTypeDto().freeLessons().getFirst().dateTo()
        );

        // Возвращаем true, если урок найден, иначе false
        return lesson != null;
    }

    public String updateStatus(SignupRequestDto signupRequest, String newStatus) {
        // Логика обновления статуса заявки
        Lesson lesson = lessonRepo.findByLessonTimeAndFromAndTo(
                signupRequest.lessonTime(),
                signupRequest.lessonTypeDto().freeLessons().getFirst().dateFrom(),
                signupRequest.lessonTypeDto().freeLessons().getFirst().dateTo()
        );
        lesson.setLessonStatus(LessonStatus.RESERVED);
        lessonRepo.save(lesson);
        System.out.println("Статус заявки обновлен на: " + newStatus);
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
                signupRequest.lessonTypeDto().freeLessons().getFirst().dateFrom(),
                signupRequest.lessonTypeDto().freeLessons().getFirst().dateTo(),
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
