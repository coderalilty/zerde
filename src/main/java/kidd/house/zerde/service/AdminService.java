package kidd.house.zerde.service;

import kidd.house.zerde.dto.adminDto.*;
import kidd.house.zerde.dto.schedule.ChildDto;
import kidd.house.zerde.dto.sendNotification.NotificationRequestDto;
import kidd.house.zerde.dto.temporartLessonDto.TemporaryLessonDtos;
import kidd.house.zerde.mapper.LessonMapper;
import kidd.house.zerde.model.entity.*;
import kidd.house.zerde.model.role.Authorities;
import kidd.house.zerde.model.status.LessonStatus;
import kidd.house.zerde.model.type.LessonType;
import kidd.house.zerde.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Service
@Slf4j
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
    private LessonRepo lessonRepo;
    @Autowired
    private LockedSlotRepo lockedSlotRepo;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private MailSenderService mailSenderService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ChildRepo childRepo;
    @Autowired
    private LessonMapper lessonMapper;
    public void createNewTeacher(CreateTeacherDto createTeacherDto) {
        User user = new User();
        user.setName(createTeacherDto.name());
        user.setSurName(createTeacherDto.surname());
        user.setLastName(createTeacherDto.lastname());
        user.setEmail(createTeacherDto.email());
        user.setPhone(createTeacherDto.phone());

        List<Subject> subject = Collections.singletonList(subjectRepo.findByName(createTeacherDto.subjectName()));
        user.setSubjects(subject);

        String rawPassword = randomAlphanumeric(8);
        user.setPasswordTemporary(true);

        String message = String.format(
                "Уважаемый(ая) %s, для вас создан аккаунт преподавателя, с email %s и временным паролем %s",
                user.getName(),
                user.getEmail(),
                rawPassword
        );
        mailSenderService.send(
                user.getEmail(),
                "Напоминание о создании аккаунта",
                message
        );
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
        Lesson lesson = new Lesson();
        lesson.setLessonDay(createLessonDto.lessonDay());
        lesson.setFrom(createLessonDto.createLessonFrom());
        lesson.setTo(createLessonDto.createLessonTo());
        lesson.setGroupType(createLessonDto.groupType());
        lesson.setLessonStatus(LessonStatus.SCHEDULED);
        lesson.setLessonType(LessonType.PERMANENT);
        Group group = groupRepo.findById(createLessonDto.groupId());
        lesson.setGroup(group);
        Subject subject = subjectRepo.findById(createLessonDto.subjectId());
        lesson.setSubject(subject);
        Room room = roomRepo.findById(createLessonDto.roomId());
        lesson.setRoom(room);
        User teacher = userRepo.findById(createLessonDto.teacherId());
        lesson.setUser(teacher);

        // === 3. Проверка, свободно ли время ===
        if (createLessonDto.groupType().equals("GROUP")){
            // Проверка: нет ли уже заглушки
            List<LockedSlot> lockedSlots = lockedSlotRepo
                    .findLockedBetween(
                            lesson.getLessonDay(),
                            lesson.getFrom(),
                            lesson.getTo(),
                            lesson.getRoom().getName()
                    );

            if (!lockedSlots.isEmpty()) {
                throw new IllegalStateException("Уже стоит индеведуальный урок на это время!");
            }
        } else if (createLessonDto.groupType().equals("INDIVIDUAL")) {
            lessonService.lockLesson(lesson.getLessonDay(),lesson.getFrom(), lesson.getTo(), lesson.getRoom().getName());
        }
        lessonRepo.save(lesson);
    }
    public void sendNotification(CreateLessonDto createLessonDto) {
        // Логика отправки уведомления (в будущем можно интегрировать WhatsApp/Telegram API)
        Group group = groupRepo.findById(createLessonDto.groupId());
        // Получаем список имён детей (для текста)
        String childNames = group.getChildren().stream()
                .map(Child::getFirstName)
                .collect(Collectors.joining(", "));

        // Формируем сообщение
        String message = String.format(
                "Уважаемый(ая) %s, у вас запланирован урок, который состоится с %s до %s.",
                childNames,
                createLessonDto.createLessonFrom(),
                createLessonDto.createLessonTo()
        );

        // Получаем список email'ов родителей
        List<String> emails = group.getChildren().stream()
                .map(c -> c.getUser().getEmail())
                .filter(Objects::nonNull)
                .toList();

        // Отправляем письма каждому родителю
        for (String email : emails) {
            mailSenderService.send(
                    email,
                    "Напоминание о предстоящем уроке",
                    message
            );
        }

        System.out.println("Отправлено уведомление родителям: " + childNames);
    }
    public List<LessonDtos> getLessons(){
        List<Lesson> lessons = lessonRepo.findAllByLessonType(LessonType.PERMANENT);
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
    public List<ChildDtos> getChildrenByLessonId(int lessonId){
        Lesson lesson = lessonRepo.findById(lessonId);
        List<Child> children = childRepo.findByGroupId(lesson.getGroup().getId());
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

    public List<ListRoomsDto> getRooms() {
        List<Room> rooms = roomRepo.findAll();
        return rooms.stream()
                .map(this::toDtoRoom)
                .toList();
    }

    private ListRoomsDto toDtoRoom(Room room) {
        return new ListRoomsDto(
                room.getId(),
                room.getName()
        );
    }

    public List<ListTeachersDto> getTeachers() {
        List<User> teachers = userRepo.findAll();
        return teachers.stream()
                .filter(user -> user.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("TEACHER")))
                .map(this::toDtoTeachers)
                .toList();
    }

    private ListTeachersDto toDtoTeachers(User user) {
        List<ListSubjectsDto> subjectDtos = user.getSubjects().stream()
                .map(subject -> new ListSubjectsDto(1,subject.getName()))
                .toList();
        return new ListTeachersDto(
                user.getName(),
                user.getSurName(),
                user.getLastName(),
                user.getEmail(),
                user.getAuthorities(),
                user.isPasswordTemporary(),
                subjectDtos
        );
    }

    public List<ListSubjectsDto> getSubjects() {
        List<Subject> subjects = subjectRepo.findAll();
        return subjects.stream()
                .map(this::toDtoSubject)
                .toList();
    }
    private ListSubjectsDto toDtoSubject(Subject subject) {
        return new ListSubjectsDto(
                subject.getId(),
                subject.getName()
        );
    }

    public List<ListGroupsDto> getGroups() {
        List<Group> groups = groupRepo.findAll();
        return groups.stream()
                .map(this::toDtoGroup)
                .toList();
    }

    private ListGroupsDto toDtoGroup(Group group) {
        return new ListGroupsDto(
                group.getId(),
                group.getName()
        );
    }

    public List<AdminProfileDto> getAdminProfiles() {
        return userRepo.findAll().stream()
                .filter(user -> user.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ADMIN")))
                .map(this::toDtoAdmin)
                .toList();
    }

    private AdminProfileDto toDtoAdmin(User user) {
        return new AdminProfileDto(
                user.getId(),
                user.getName(),
                user.getSurName(),
                user.getLastName(),
                user.getEmail(),
                user.getAuthorities()
        );
    }

    public void createNewChild(ChildDtos childDtos) {
        Child child = new Child();
        child.setFirstName(childDtos.firstName());
        child.setMiddleName(childDtos.middleName());
        child.setLastName(childDtos.lastName());
        child.setAge(childDtos.age());
        childRepo.save(child);
    }

    public List<ChildDtos> getChildren() {
        List<Child> children = childRepo.findAll();
        return children.stream()
                .map(this::toDtoChild)
                .toList();
    }

    public void editChild(int childId, ChildDtos childDtos) {
        Child child = childRepo.findById(childId);
        if (childDtos.firstName() != null){
            child.setFirstName(childDtos.firstName());
        }
        if (childDtos.middleName() != null){
            child.setMiddleName(childDtos.middleName());
        }
        if (childDtos.lastName() != null){
            child.setLastName(childDtos.lastName());
        }
        if (childDtos.age() != 0){
            child.setAge(childDtos.age());
        }
        childRepo.save(child);
    }

    public void deleteChild(int childId) {
        childRepo.deleteById(childId);
    }

    public void editTeacher(int teacherId, CreateTeacherDto createTeacherDto) {
        User teacher = userRepo.findById(teacherId);
        if (createTeacherDto.name() != null){
            teacher.setName(createTeacherDto.name());
        }
        if (createTeacherDto.surname() != null){
            teacher.setSurName(createTeacherDto.surname());
        }
        if (createTeacherDto.lastname() != null){
            teacher.setLastName(createTeacherDto.lastname());
        }
        if (createTeacherDto.phone() != null){
            teacher.setPhone(createTeacherDto.phone());
        }
        if (createTeacherDto.subjectName() != null) {
            teacher.getSubjects().get(0).setName(createTeacherDto.subjectName());
        }
        userRepo.save(teacher);
    }

    public void deleteTeacher(int teacherId) {
        User userToDelete = userRepo.findById(teacherId);

        // Получаем текущего аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Проверка 1: нельзя удалить самого себя
        if (userToDelete.getUsername().equals(currentUsername)) {
            log.warn("Нельзя удалить самого себя");
            return;
        }

        // Проверка 2: нельзя удалить администратора
        boolean isAdmin = userToDelete.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

        if (isAdmin) {
            log.warn("Нельзя удалить администратора");
            return;
        }

        // Если проверки пройдены — удаляем
        userRepo.deleteById(teacherId);
        log.info("Пользователь с ID {} успешно удален", teacherId);
    }


    public void editSubject(int subjectId, CreateSubjectDto createSubjectDto) {
        Subject subject = subjectRepo.findById(subjectId);
        if (createSubjectDto.subjectName() != null){
            subject.setName(createSubjectDto.subjectName());
        }
        subjectRepo.save(subject);
    }

    public void editRoom(int roomId, CreateRoomDto createRoomDto) {
        Room room = roomRepo.findById(roomId);
        if (createRoomDto.roomName() != null){
            room.setName(createRoomDto.roomName());
        }
        roomRepo.save(room);
    }

    public void editGroup(int appGroupId, EditGroupDto editGroupDto) {
        Group group = groupRepo.findById(appGroupId);

        if (editGroupDto.groupName() != null) {
            group.setName(editGroupDto.groupName());
        }

        if (editGroupDto.childIds() != null && !editGroupDto.childIds().isEmpty()) {
            List<Child> children = childRepo.findAllById(editGroupDto.childIds());
            group.getChildren().addAll(children); // ✅ добавляем, не затираем
        }

        groupRepo.save(group);
    }

    public void deleteGroup(int appGroupId) {
        groupRepo.deleteById(appGroupId);
    }

    public void deleteRoom(int roomId) {
        roomRepo.deleteById(roomId);
    }

    public void deleteSubject(int subjectId) {
        subjectRepo.deleteById(subjectId);
    }

    public List<TemporaryLessonDtos> getTrailLesson() {
        return lessonRepo.findAllByLessonType(LessonType.TRIAL)
                .stream()
                .map(this::toDtoTrialLesson)
                .toList();
    }


    private TemporaryLessonDtos toDtoTrialLesson(Lesson lesson) {
        String childNames = lesson.getGroup().getChildren().stream()
                .map(Child::getFirstName)
                .collect(Collectors.joining(", "));

        String parentNames = lesson.getGroup().getChildren().stream()
                .map(c -> c.getUser().getName())
                .collect(Collectors.joining(", "));

        String parentPhones = lesson.getGroup().getChildren().stream()
                .map(c -> c.getUser().getPhone())
                .collect(Collectors.joining(", "));

        String parentEmails = lesson.getGroup().getChildren().stream()
                .map(c -> c.getUser().getEmail())
                .collect(Collectors.joining(", "));

        List<Integer> ages = lesson.getGroup().getChildren().stream()
                .map(Child::getAge)
                .toList();

        return new TemporaryLessonDtos(
                childNames,
                ages,
                parentNames,
                parentPhones,
                parentEmails,
                lesson.getLessonDay(),
                lesson.getFrom(),
                lesson.getTo()
        );
    }

    public void sendNotificationAll(NotificationRequestDto notificationRequest) {
        int lessonId = notificationRequest.lessonId();
        Lesson lesson = lessonService.findById(lessonId);
        // Поиск урока по lessonId через сервис
        if (lesson != null) {
            log.warn("Lesson not found 404");
        }

        List<ChildDto> childFirstName = lessonMapper.getChildFirstName(lesson);

        // Формирование сообщения
        String message = String.format(
                "Уважаемый(ая) %s, у вас запланирован урок с преподавателем %s, который состоится с %s до %s в комнате %s.",
                childFirstName,
                "Gregory",
                lesson.getFrom(),
                lesson.getTo(),
                lesson.getRoom().getName()
        );
        try {
            // Отправка email родителю, если указан email
            if (lesson.getGroup().getChildren().get(0).getUser().getEmail() != null) {
                mailSenderService.send(
                        lesson.getGroup().getChildren().get(0).getUser().getEmail(),
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
            log.warn("Failed to send notification");
        }
    }
}
