package kidd.house.zerde.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kidd.house.zerde.dto.adminDto.*;
import kidd.house.zerde.dto.lockLesson.LockLessonRequest;
import kidd.house.zerde.mapper.LessonMapper;
import kidd.house.zerde.service.AdminService;
import kidd.house.zerde.service.EmailKafkaProducer;
import kidd.house.zerde.service.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {
    @Mock
    private LessonService lessonService;
    @Mock
    private LessonMapper lessonMapper;
    @Mock
    private EmailKafkaProducer emailKafkaProducer;
    @Mock
    private AdminService adminService;
    @InjectMocks
    private AdminController adminController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        objectMapper = new ObjectMapper();
    }

    @Test//get
    void schedule() {
    }

    @Test//get
    void weekSchedule() {
    }

    @Test//
    void lockLesson() throws Exception {
        LockLessonRequest lockLessonRequest = new LockLessonRequest("10:00","10:30","202");
        String lessonJson = objectMapper.writeValueAsString(lockLessonRequest);
        mockMvc.perform(post("/api/v1/admin/lock-lesson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(lessonJson))
                .andExpect(status().isLocked());
        verify(lessonService,times(1)).lockLesson(lockLessonRequest.lockDateTimeFrom(), lockLessonRequest.lockDateTimeTo(), lockLessonRequest.roomName());

    }

    @Test//
    void sendNotification() throws Exception {
//        Parent parent = new Parent();
//        parent.setParentName("Gregory");
//        parent.setParentPhone("+77788136226");
//        parent.setParentEmail("elzat.sayatov.3@gmail.com");
//
//        Child child = new Child();
//        child.setFirstName("Petya");
//        child.setAge(12);
//        child.setParent(parent);
//
//        Room room = new Room();
//        room.setName("202");
//
//        Lesson lesson = new Lesson();
//        lesson.setLessonName("bukva b");
//        lesson.setFrom("10:00");
//        lesson.setTo("10:30");
//        lesson.setId(1);
//        lesson.setGroupType("GROUP");
//        lesson.setLessonDay("2025.06.17");
//        child.setLesson(lesson);
//        lesson.getChildren().add(child);
//        lesson.setRoom(room);
//        String message = String.format(
//                "Уважаемый(ая) %s, у вас запланирован урок с преподавателем %s, который состоится с %s до %s в комнате %s.",
//                child.getFirstName(),
//                "Gregory",
//                lesson.getFrom(),
//                lesson.getTo(),
//                room.getName()
//        );
//
//        NotificationRequestDto notificationRequestDto = new NotificationRequestDto(lesson.getId());
//        String value = objectMapper.writeValueAsString(notificationRequestDto);
//
//        mockMvc.perform(post("/api/v1/admin/send-notification")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(value))
//                .andExpect(status().isOk());
//        verify(lessonService,times(1)).findById(lesson.getId());
//        verify(lessonMapper,times(1)).getChildFirstName(lesson);
//        verify(emailKafkaProducer, times(1)).sendEmail(new EmailMessageDto(
//                parent.getParentEmail(),
//                "Напоминание о предстоящем уроке",
//                message
//        ));
    }

    @Test//get
    void getAllLessons() {
    }

    @Test//get
    void getChildList() {
    }

    @Test//
    void createTeacher() throws Exception {
        CreateTeacherDto teacherDto = new CreateTeacherDto("Gregory","Anna","Aleksandra","gregory.annd@gmail.com");
        String value = objectMapper.writeValueAsString(teacherDto);

        mockMvc.perform(post("/api/v1/admin/create-teacher")
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(status().isCreated());

        verify(adminService,times(1)).createNewTeacher(teacherDto);
    }

    @Test//
    void createSubject() throws Exception {
        CreateSubjectDto subjectDto = new CreateSubjectDto("Bukva B");
        String value = objectMapper.writeValueAsString(subjectDto);

        mockMvc.perform(post("/api/v1/admin/create-subject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(status().isCreated());

        verify(adminService, times(1)).createNewSubject(subjectDto);
    }

    @Test//
    void createRoom() throws Exception {
        CreateRoomDto roomDto = new CreateRoomDto("202");
        String value = objectMapper.writeValueAsString(roomDto);

        mockMvc.perform(post("/api/v1/admin/create-room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(status().isCreated());

        verify(adminService, times(1)).createNewRoom(roomDto);
    }

    @Test//
    void createGroup() throws Exception {
        CreateGroupDto createGroupDto = new CreateGroupDto("IT-001");
        String value = objectMapper.writeValueAsString(createGroupDto);

        mockMvc.perform(post("/api/v1/admin/create-group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(status().isCreated());

        verify(adminService, times(1)).createNewGroup(createGroupDto);
    }

    @Test//
    void createLesson() throws  Exception {
        CreateLessonDto createLessonDto = new CreateLessonDto(
                "10:00","10:30","GROUP",1,1,1,
                1,"Vita","Vasylych","",12,
                "Lesya","Serina","","+7774576849","elzat.sayatov.3@gmail.com"
        );
        String value = objectMapper.writeValueAsString(createLessonDto);

        mockMvc.perform(post("/api/v1/admin/create-lesson")
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(status().isCreated());

        verify(adminService, times(1)).createNewLesson(createLessonDto);
        verify(adminService, times(1)).sendNotification(createLessonDto);
    }
}