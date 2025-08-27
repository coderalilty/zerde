package kidd.house.zerde.service;

import kidd.house.zerde.dto.adminDto.CreateGroupDto;
import kidd.house.zerde.dto.adminDto.CreateRoomDto;
import kidd.house.zerde.dto.adminDto.CreateSubjectDto;
import kidd.house.zerde.model.entity.Group;
import kidd.house.zerde.model.entity.Room;
import kidd.house.zerde.model.entity.Subject;
import kidd.house.zerde.repo.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @InjectMocks
    private AdminService adminService;
    @Mock
    private SubjectRepo subjectRepo;
    @Mock
    private RoomRepo roomRepo;
    @Mock
    private GroupRepo groupRepo;
    @Mock
    private LessonRepo lessonRepo;
    @Mock
    private ChildRepo childRepo;

//    @Test
//    void createNewTeacher() {
//        CreateTeacherDto teacherDto = new CreateTeacherDto(
//                "Gregory",
//                "Aleksandr",
//                "Andreyevich",
//                "gregory@gmail.com");
//        User user = new User();
//        user.setName(teacherDto.name());
//        user.setSurName(teacherDto.surname());
//        user.setLastName(teacherDto.lastname());
//        user.setEmail(teacherDto.email());
//        String rawPassword = randomAlphanumeric(8);
//        user.setPasswordTemporary(true);
//        user.setPassword(passwordEncoder.encode(rawPassword));
//        user.setAuthorities(Authorities.TEACHER);
//
//        adminService.createNewTeacher(teacherDto);
//
//        Mockito.verify(userRepo,Mockito.times(1)).save(user);
//
//    }

    @Test
    void createNewSubject() {
        CreateSubjectDto createSubjectDto = new CreateSubjectDto("bukva V");
        Subject subject = new Subject();
        subject.setName(createSubjectDto.subjectName());

        adminService.createNewSubject(createSubjectDto);

        Mockito.verify(subjectRepo,Mockito.times(1)).save(subject);
    }

    @Test
    void createNewRoom() {
        CreateRoomDto createRoomDto = new CreateRoomDto("202");
        Room room =new Room();
        room.setName(createRoomDto.roomName());

        adminService.createNewRoom(createRoomDto);

        Mockito.verify(roomRepo,Mockito.times(1)).save(room);
    }

    @Test
    void createNewGroup() {
        CreateGroupDto createGroupDto = new CreateGroupDto("IT-001");
        Group group = new Group();
        group.setName(createGroupDto.groupName());

        adminService.createNewGroup(createGroupDto);

        Mockito.verify(groupRepo,Mockito.times(1)).save(group);
    }

    @Test
    void createNewLesson() {
//        CreateLessonDto createLessonDto = new CreateLessonDto(
//                "10:00","10:30","GROUP",1,1,1,
//                1,"Vita","Vasylych","",12,
//                "Lesya","Serina","","+7774576849","elzat.sayatov.3@gmail.com"
//        );
//        Parent parent = new Parent();
//        parent.setParentName(createLessonDto.parentName());
//        parent.setMiddleName(createLessonDto.parentSurName());
//        parent.setLastName(createLessonDto.parentLastName());
//        parent.setParentPhone(createLessonDto.parentPhone());
//        parent.setParentEmail(createLessonDto.parentEmail());
//
//
//        Child child = new Child();
//        child.setFirstName(createLessonDto.childName());
//        child.setMiddleName(createLessonDto.childSurName());
//        child.setLastName(createLessonDto.childLastName());
//        child.setAge(createLessonDto.childAge());
//        child.setParent(parent);
//
//        Lesson lesson = new Lesson();
//        lesson.setFrom(createLessonDto.createLessonFrom());
//        lesson.setTo(createLessonDto.createLessonTo());
//        lesson.setGroupType(createLessonDto.groupType());
//        lesson.setGroup(groupRepo.findById(createLessonDto.groupId()));
//        lesson.setLessonStatus(LessonStatus.SCHEDULED);
//        lesson.setLessonType(LessonType.PERMANENT);
//        lesson.setSubject(subjectRepo.findById(createLessonDto.subjectId()));
//        lesson.setRoom(roomRepo.findById(createLessonDto.roomId()));
//        lesson.setUser(userRepo.findById(createLessonDto.teacherId()));
//
//        child.setLesson(lesson);
//        lesson.getChildren().add(child);
//
//        adminService.createNewLesson(createLessonDto);
//
//
//        Mockito.verify(parentRepo,Mockito.times(1)).save(parent);
//        Mockito.verify(lessonRepo,Mockito.times(1)).save(lesson);
    }

    @Test
    void sendNotification() {
//        CreateLessonDto createLessonDto = new CreateLessonDto(
//                "10:00","10:30","GROUP",1,1,1,
//                1,"Vita","Vasylych","",12,
//                "Lesya","Serina","","+7774576849","elzat.sayatov.3@gmail.com"
//        );
//        String message = String.format(
//                "Уважаемый(ая) %s, у вас запланирован урок, который состоится с %s до %s.",
//                createLessonDto.childName(),
//                createLessonDto.createLessonFrom(),
//                createLessonDto.createLessonTo()
//        );
//        EmailMessageDto emailMessageDto = new EmailMessageDto(
//                createLessonDto.parentEmail(),
//                "Напоминание о предстоящем уроке",
//                message
//        );
//        Parent parent = parentRepo.findByParentPhoneAndParentEmail(
//                createLessonDto.parentPhone(),
//                createLessonDto.parentEmail()
//        );
//
//        Mockito.when(parentRepo.findByParentPhoneAndParentEmail(createLessonDto.parentPhone(),
//                createLessonDto.parentEmail())).thenReturn(parent);
//        adminService.sendNotification(createLessonDto);
//        Mockito.verify(parentRepo, Mockito.times(1)).findByParentPhoneAndParentEmail(createLessonDto.parentPhone(),
//                createLessonDto.parentEmail());
//        Mockito.verify(emailKafkaProducer, Mockito.times(1)).sendEmail(
//              emailMessageDto
//        );
    }

    @Test
    void getLessons() {
        adminService.getLessons();
        Mockito.verify(lessonRepo, Mockito.times(1)).findAll();
    }

    @Test
    void getChildrenByLessonId() {
        adminService.getChildrenByLessonId(1L);
        Mockito.verify(childRepo,Mockito.times(1)).findByLessonId(1L);
    }
}