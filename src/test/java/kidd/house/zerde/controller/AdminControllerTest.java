package kidd.house.zerde.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kidd.house.zerde.dto.adminDto.*;
import kidd.house.zerde.dto.lockLesson.LockLessonRequest;
import kidd.house.zerde.service.AdminService;
import kidd.house.zerde.service.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {
    @Mock
    private LessonService lessonService;
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
                .andExpect(status().isOk());
        verify(lessonService,times(1)).lockLesson(lockLessonRequest.lockDateTimeFrom(), lockLessonRequest.lockDateTimeTo(), lockLessonRequest.roomName());

    }

    @Test//
    void sendNotification() {
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
    void getAllLessons() throws Exception{
        LessonDtos lessonDtos = new LessonDtos("lesson name","10:00","11:00",
                "20.05.2025","GROUP","IT-001","202","subject");
        List<LessonDtos> lessonDtosList = List.of(lessonDtos);

        Mockito.when(adminService.getLessons()).thenReturn(lessonDtosList);

        mockMvc.perform(get("/api/v1/admin/lessons")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
//    @Test//get
//    void getChildList() throws Exception{
//        ChildDtos childDtos = new ChildDtos("Petr","Vasylich","",6);
//        List<ChildDtos> childDtosList = List.of(childDtos);
//
//        Mockito.when(adminService.getChildrenByLessonId(1L)).thenReturn(childDtosList);
//
//        mockMvc.perform(get("/api/v1/admin/lessons/{lessonId}/children")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }

    @Test//
    void createTeacher() throws Exception {
        CreateTeacherDto teacherDto = new CreateTeacherDto("Gregory","Anna","Aleksandra","gregory.annd@gmail.com","+77715648955","ehbgtjntk");
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
                1
        );
        String value = objectMapper.writeValueAsString(createLessonDto);

        mockMvc.perform(post("/api/v1/admin/create-lesson")
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(status().isCreated());

        verify(adminService, times(1)).createNewLesson(createLessonDto);
        verify(adminService, times(1)).sendNotification(createLessonDto);
    }
    @Test
    void getRooms() throws Exception {
        ListRoomsDto listRoomsDto = new ListRoomsDto(1,"202");
        List<ListRoomsDto> listRoomsDtos = List.of(listRoomsDto);

        Mockito.when(adminService.getRooms()).thenReturn(listRoomsDtos);

        mockMvc.perform(get("/api/v1/admin/rooms")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getTeachers() throws Exception{
        ListSubjectsDto listSubjectsDto = new ListSubjectsDto(1,"match");
        List<ListSubjectsDto> listSubjectsDtos = List.of(listSubjectsDto);

        GrantedAuthority teacherRole = new SimpleGrantedAuthority("TEACHER");

        ListTeachersDto listTeachersDto = new ListTeachersDto("Petr","Sergeyovich","",
                "petr.gregorivich@mail.ru",teacherRole,true,listSubjectsDtos);
        List<ListTeachersDto> listTeachersDtos = List.of(listTeachersDto);

        Mockito.when(adminService.getTeachers()).thenReturn(listTeachersDtos);

        mockMvc.perform(get("/api/v1/admin/teachers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getSubjects() throws Exception {
        ListSubjectsDto listSubjectsDto = new ListSubjectsDto(1,"match");
        List<ListSubjectsDto> listSubjectsDtos = List.of(listSubjectsDto);

        Mockito.when(adminService.getSubjects()).thenReturn(listSubjectsDtos);

        mockMvc.perform(get("/api/v1/admin/subjects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getGroups() throws Exception{
        ListGroupsDto listGroupsDto = new ListGroupsDto(1,"IT-001");
        List<ListGroupsDto> listGroupsDtos = List.of(listGroupsDto);

        Mockito.when(adminService.getGroups()).thenReturn(listGroupsDtos);

        mockMvc.perform(get("/api/v1/admin/groups")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getLockLesson() throws Exception{
        LockLessonDto lockLessonDto = new LockLessonDto(1,"20.05.2025 10:00","20.05.2025 11:00","202");
        List<LockLessonDto> lockLessonDtos = List.of(lockLessonDto);

        Mockito.when(lessonService.getLockLesson()).thenReturn(lockLessonDtos);

        mockMvc.perform(get("/api/v1/admin/lock-lesson")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAdminProfiles() throws Exception {
        GrantedAuthority adminRole = new SimpleGrantedAuthority("ADMIN");

        AdminProfileDto adminProfileDto = new AdminProfileDto(1,"Admin","Adminovich","",
                "admin@mail.ru",adminRole);
        List<AdminProfileDto> adminProfileDtos = List.of(adminProfileDto);

        Mockito.when(adminService.getAdminProfiles()).thenReturn(adminProfileDtos);

        mockMvc.perform(get("/api/v1/admin/profile")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createChild() throws Exception{
        ChildDtos childDtos = new ChildDtos("Askar","Aukenov","Amanuly",26);

        String value = objectMapper.writeValueAsString(childDtos);

        mockMvc.perform(post("/api/v1/admin/create-child")
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(status().isCreated());

        verify(adminService,times(1)).createNewChild(childDtos);
    }

    @Test
    void editLesson() {
    }

    @Test
    void editChild() {
    }

    @Test
    void editTeacher() {
    }

    @Test
    void editSubject() {
    }

    @Test
    void editRoom() {
    }

    @Test
    void editGroup() {
    }

    @Test
    void deleteLockLesson() {
    }

    @Test
    void deleteLesson() {
    }

    @Test
    void deleteChild() {
    }

    @Test
    void deleteTeacher() {
    }

    @Test
    void deleteGroup() {
    }
}