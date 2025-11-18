package kidd.house.zerde.service;

import kidd.house.zerde.dto.temporartLessonDto.TemporaryLessonDto;
import kidd.house.zerde.model.entity.Child;
import kidd.house.zerde.model.entity.Group;
import kidd.house.zerde.model.entity.Lesson;
import kidd.house.zerde.model.entity.User;
import kidd.house.zerde.model.role.Authorities;
import kidd.house.zerde.model.status.LessonStatus;
import kidd.house.zerde.model.type.LessonType;
import kidd.house.zerde.repo.GroupRepo;
import kidd.house.zerde.repo.LessonRepo;
import kidd.house.zerde.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Service
@RequiredArgsConstructor
public class TrialLessonService {
    @Autowired
    private MailSenderService mailSenderService;
    @Autowired
    private LessonRepo lessonRepo;
    @Autowired
    private GroupRepo groupRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public void createTrialLessonAndSent(TemporaryLessonDto temporaryLessonDto) {
        Child child = new Child();
        child.setFirstName(temporaryLessonDto.childName());
        child.setAge(temporaryLessonDto.childAge());
        List<Child> children = List.of(child);

        Group group = new Group();
        group.setName("trial");
        group.setChildren(children);
        groupRepo.save(group);

        User parent = new User();
        parent.setName(temporaryLessonDto.parentName());
        parent.setPhone(temporaryLessonDto.parentPhone());
        parent.setEmail(temporaryLessonDto.parentEmail());
        parent.setAuthorities(Authorities.USER);

        String rawPassword = randomAlphanumeric(8);
        parent.setPasswordTemporary(true);
        parent.setPassword(passwordEncoder.encode(rawPassword));

        child.setUser(parent);
        userRepo.save(parent);

        Lesson lesson = new Lesson();
        lesson.setGroup(group);
        lesson.setLessonDay(temporaryLessonDto.lessonDay());
        lesson.setFrom(temporaryLessonDto.createTimeFrom());
        lesson.setTo(temporaryLessonDto.createTimeTo());
        lesson.setGroupType("GROUP");
        lesson.setLessonStatus(LessonStatus.TEMPORARY);
        lesson.setLessonType(LessonType.TRIAL);

        /*
        TODO: надо будет добавить ответственного за пробные уроки
        TODO: комнаты какие будет
        TODO: қай күндері ғана
         */
        lessonRepo.save(lesson);

        sendNotification(temporaryLessonDto, rawPassword);
    }

    private void sendNotification(TemporaryLessonDto temporaryLessonDto, String rawPassword) {
        String message = String.format(
                "Уважаемый(ая) %s, у вашего ребенка %s запланирован урок с преподавателем %s, который состоится с %s до %s в комнате %s. \n" +
                        "Ваш логин %s и временный пароль %s для входа ",
                temporaryLessonDto.parentName(),
                temporaryLessonDto.childName(),
                "Gregory",
                temporaryLessonDto.createTimeFrom(),
                temporaryLessonDto.createTimeTo(),
                "202",
                temporaryLessonDto.parentEmail(),
                rawPassword
                );
        try {
            // Отправка email родителю, если указан email
            if (temporaryLessonDto.parentEmail() != null) {
                mailSenderService.send(
                        temporaryLessonDto.parentEmail(),
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
            ResponseEntity.status(500).body("Failed to send notification");
        }
    }
}
