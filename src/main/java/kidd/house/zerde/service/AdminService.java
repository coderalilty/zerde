package kidd.house.zerde.service;

import kidd.house.zerde.dto.adminDto.*;
import kidd.house.zerde.dto.sendNotification.EmailMessageDto;
import kidd.house.zerde.model.entity.*;
import kidd.house.zerde.model.role.Authorities;
import kidd.house.zerde.model.status.LessonStatus;
import kidd.house.zerde.model.type.LessonType;
import kidd.house.zerde.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.*;

@Service
@RequiredArgsConstructor
public class AdminService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SubjectRepo subjectRepo;
    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private GroupRepo groupRepo;
    @Autowired
    private ParentRepo parentRepo;
    @Autowired
    private LessonRepo lessonRepo;
    @Autowired
    private LockedSlotRepo lockedSlotRepo;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private EmailKafkaProducer emailKafkaProducer;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ChildRepo childRepo;
    public void createNewTeacher(CreateTeacherDto createTeacherDto) {
        User user = new User();
        user.setName(createTeacherDto.name());
        user.setSurName(createTeacherDto.surname());
        user.setLastName(createTeacherDto.lastname());
        user.setEmail(createTeacherDto.email());

        String rawPassword = randomAlphanumeric(8);
        user.setPasswordTemporary(true);

        String message = String.format(
                "Уважаемый(ая) %s, для вас создан аккаунт преподавателя, с email %s и временным паролем %s",
                user.getName(),
                user.getEmail(),
                rawPassword
        );
        emailKafkaProducer.sendEmail(new EmailMessageDto(
                user.getEmail(),
                "Напоминание о создании аккаунта",
                message
        ));
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setAuthorities(Authorities.TEACHER);
        userRepo.save(user);
    }
    public void createNewSubject(CreateSubjectDto createSubjectDto) {
        Subject subject = new Subject();
        subject.setName(createSubjectDto.subjectName());
        subjectRepo.save(subject);
    }
    public void createNewRoom(CreateRoomDto createRoomDto) {
        Room room = new Room();
        room.setName(createRoomDto.roomName());
        roomRepo.save(room);
    }
    public void createNewGroup(CreateGroupDto createGroupDto) {
        Group group = new Group();
        group.setName(createGroupDto.groupName());
        groupRepo.save(group);
    }

    public void createNewLesson(CreateLessonDto createLessonDto) {
        Parent parent = new Parent();
        parent.setParentName(createLessonDto.parentName());
        parent.setMiddleName(createLessonDto.parentSurName());
        parent.setLastName(createLessonDto.parentLastName());
        parent.setParentPhone(createLessonDto.parentPhone());
        parent.setParentEmail(createLessonDto.parentEmail());


        Child child = new Child();
        child.setFirstName(createLessonDto.childName());
        child.setMiddleName(createLessonDto.childSurName());
        child.setLastName(createLessonDto.childLastName());
        child.setAge(createLessonDto.childAge());
        child.setParent(parent);

        Lesson lesson = new Lesson();
        lesson.setFrom(createLessonDto.createLessonFrom());
        lesson.setTo(createLessonDto.createLessonTo());
        lesson.setGroupType(createLessonDto.groupType());
        lesson.setGroup(groupRepo.findById(createLessonDto.groupId()));
        lesson.setLessonStatus(LessonStatus.SCHEDULED);
        lesson.setLessonType(LessonType.PERMANENT);
        lesson.setSubject(subjectRepo.findById(createLessonDto.subjectId()));
        lesson.setRoom(roomRepo.findById(createLessonDto.roomId()));
        lesson.setUser(userRepo.findById(createLessonDto.teacherId()));

        child.setLesson(lesson);
        lesson.getChildren().add(child);
        // === 3. Проверка, свободно ли время ===
        if (createLessonDto.groupType().equals("GROUP")){
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
        } else if (createLessonDto.groupType().equals("INDIVIDUAL")) {
            lessonService.lockLesson(lesson.getFrom(), lesson.getTo(), lesson.getRoom().getName());
        }
        parentRepo.save(parent);
        lessonRepo.save(lesson);
    }
    public void sendNotification(CreateLessonDto createLessonDto) {
        // Логика отправки уведомления (в будущем можно интегрировать WhatsApp/Telegram API)
        Parent parent = parentRepo.findByParentPhoneAndParentEmail(
                createLessonDto.parentPhone(),
                createLessonDto.parentEmail()
        );

        // Формирование сообщения
        String message = String.format(
                "Уважаемый(ая) %s, у вас запланирован урок, который состоится с %s до %s.",
                createLessonDto.childName(),
                createLessonDto.createLessonFrom(),
                createLessonDto.createLessonTo()
        );
        // Отправка email родителю, если указан email
        if (parent.getParentEmail() != null) {
            emailKafkaProducer.sendEmail(new EmailMessageDto(
                    parent.getParentEmail(),
                    "Напоминание о предстоящем уроке",
                    message
            ));
        }
        System.out.println("Отправка уведомления для заявки: " + createLessonDto.childName());
    }
    public List<LessonDtos> getLessons(){
        List<Lesson> lessons = lessonRepo.findAll();
        return toDtoListLesson(lessons);
    }
    private List<LessonDtos> toDtoListLesson(List<Lesson> lessons) {
        return lessons.stream()
                .filter(lesson -> lesson.getLessonType() == LessonType.PERMANENT)
                .map(this::toDto)
                .toList();
    }
    private LessonDtos toDto(Lesson lesson) {
        return new LessonDtos(
                lesson.getLessonName(),
                lesson.getFrom(),
                lesson.getTo(),
                lesson.getLessonDay(),
                lesson.getGroupType(),
                lesson.getGroup().getName(),
                lesson.getRoom().getName(),
                lesson.getSubject().getName()
        );
    }
    public List<ChildDtos> getChildrenByLessonId(Long lessonId){
        List<Child> children = childRepo.findByLessonId(lessonId);
        return toDtoListChild(children);
    }
    private List<ChildDtos> toDtoListChild(List<Child> children) {
        return children.stream()
                .map(this::toDtoChild)
                .toList();
    }
    private ChildDtos toDtoChild(Child child) {
        return new ChildDtos(
                child.getFirstName(),
                child.getMiddleName(),
                child.getLastName(),
                child.getAge()
        );
    }
}
