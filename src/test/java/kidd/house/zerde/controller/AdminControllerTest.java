package kidd.house.zerde.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kidd.house.zerde.dto.adminDto.*;
import kidd.house.zerde.mapper.LessonMapper;
import kidd.house.zerde.model.entity.*;
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

import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {
    @Mock
    private LessonService lessonService;
    @Mock
    private LessonMapper lessonMapper;
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
//    @Test//
//    void lockLesson() throws Exception {
//        LockLessonRequest lockLessonRequest = new LockLessonRequest("25.05.2025","10:00","10:30","202");
//        String lessonJson = objectMapper.writeValueAsString(lockLessonRequest);
//        mockMvc.perform(post("/api/v1/admin/lock-lesson")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(lessonJson))
//                .andExpect(status().isOk());
//        verify(lessonService,times(1)).lockLesson(lockLessonRequest.lockLessonDay(), lockLessonRequest.lockDateTimeFrom(), lockLessonRequest.lockDateTimeTo(), lockLessonRequest.roomName());
//
//    }

    @Test//
    void sendNotification() {
    }

    @Test//get
    void getAllLessons() throws Exception{
        LessonDtos lessonDtos = new LessonDtos("lesson name","10:00","11:00",
                "20.05.2025","GROUP","IT-001","202","subject");
        List<LessonDtos> lessonDtosList = List.of(lessonDtos);

        Mockito.when(adminService.getLessons()).thenReturn(lessonDtosList);

        mockMvc.perform(get("/api/v1/admin/permanent_lessons")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test//get
    void getChildList() throws Exception{
        Lesson lesson = new Lesson();
        lesson.setId(1);
        lesson.setLessonName("fg");
        lesson.setFrom("grt");

        ChildDtos childDto = new ChildDtos("Petr","Vasylich","",6);
        List<ChildDtos> childDtos = List.of(childDto);

        Mockito.when(adminService.getChildrenByLessonId(lesson.getId())).thenReturn(childDtos);

        mockMvc.perform(get("/api/v1/admin/lessons/{lessonId}/children",lesson.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

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
                "25.05.2025",
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
        Collection<? extends GrantedAuthority> authorities = List.of(teacherRole);

        ListTeachersDto listTeachersDto = new ListTeachersDto("Petr","Sergeyovich","",
                "petr.gregorivich@mail.ru", authorities,true,listSubjectsDtos);
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

//    @Test
//    void getLockLesson() throws Exception{
//        LockLessonDto lockLessonDto = new LockLessonDto(1,"20.05.2025 10:00","20.05.2025 11:00","202");
//        List<LockLessonDto> lockLessonDtos = List.of(lockLessonDto);
//
//        Mockito.when(lessonService.getLockLesson()).thenReturn(lockLessonDtos);
//
//        mockMvc.perform(get("/api/v1/admin/lock-lesson")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }

    @Test
    void getAdminProfiles() throws Exception {
        GrantedAuthority adminRole = new SimpleGrantedAuthority("ADMIN");
        Collection<? extends GrantedAuthority> authorities = List.of(adminRole);


        AdminProfileDto adminProfileDto = new AdminProfileDto(1,"Admin","Adminovich","",
                "admin@mail.ru", authorities);
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
    void editLesson() throws Exception{
        Lesson lesson = new Lesson();
        lesson.setId(1);

        LessonDtos lessonDtos = new LessonDtos("lesson name","10:00","11:00",
                "20.05.2025","GROUP","IT-001","202","subject");

        String value = objectMapper.writeValueAsString(lessonDtos);

        mockMvc.perform(put("/api/v1/admin/edit_lesson/{lesson_id}",lesson.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(status().isOk());

        verify(lessonService,times(1)).editLesson(lesson.getId(),lessonDtos);
    }

    @Test
    void editChild() throws Exception {
        Child child = new Child();
        child.setId(1);

        ChildDtos childDto = new ChildDtos("Petr","Vasylich","",6);

        String value = objectMapper.writeValueAsString(childDto);

        mockMvc.perform(put("/api/v1/admin/edit_child/{child_id}",child.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(status().isOk());

        verify(adminService,times(1)).editChild(child.getId(),childDto);
    }

    @Test
    void editTeacher() throws Exception {
        User teacher = new User();
        teacher.setId(1);

        CreateTeacherDto teacherDto = new CreateTeacherDto("Gregory","Anna","Aleksandra","gregory.annd@gmail.com","+77715648955","ehbgtjntk");

        String value = objectMapper.writeValueAsString(teacherDto);

        mockMvc.perform(put("/api/v1/admin/edit_teacher/{teacher_id}",teacher.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(status().isOk());

        verify(adminService,times(1)).editTeacher(teacher.getId(),teacherDto);
    }

    @Test
    void editSubject() throws Exception{
        Subject subject = new Subject();
        subject.setId(1);

        CreateSubjectDto subjectDto = new CreateSubjectDto("gfht");

        String value = objectMapper.writeValueAsString(subjectDto);

        mockMvc.perform(put("/api/v1/admin/edit-subject/{subject_id}",subject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(status().isOk());

        verify(adminService,times(1)).editSubject(subject.getId(),subjectDto);
    }

    @Test
    void editRoom() throws Exception {
        Room room = new Room();
        room.setId(1);

        CreateRoomDto createRoomDto = new CreateRoomDto("202");

        String value = objectMapper.writeValueAsString(createRoomDto);

        mockMvc.perform(put("/api/v1/admin/edit-room/{room_id}",room.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(status().isOk());

        verify(adminService,times(1)).editRoom(room.getId(),createRoomDto);
    }

    @Test
    void editGroup() throws Exception {
        Group group = new Group();
        group.setId(1);

        // Новый DTO теперь принимает и имя группы, и список ID детей
        EditGroupDto editGroupDto = new EditGroupDto("gtg2150", List.of(1, 2, 3));

        String value = objectMapper.writeValueAsString(editGroupDto);

        mockMvc.perform(put("/api/v1/admin/edit-group/{app_group_id}", group.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(value))
                .andExpect(status().isOk());

        verify(adminService, times(1)).editGroup(group.getId(), editGroupDto);
    }


    @Test
    void deleteLockLesson() throws Exception {
        LockLessonDto lockLessonDto = new LockLessonDto(1,"20.05.2025 10:00","20.05.2025 11:00","202");

        String value = objectMapper.writeValueAsString(lockLessonDto);

        mockMvc.perform(delete("/api/v1/admin/lock-lesson/{lockLesson_id}",lockLessonDto.lesson_id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(value))
                .andExpect(status().isOk());

        verify(lessonService,times(1)).deleteLockLesson(lockLessonDto.lesson_id());
    }

    @Test
    void deleteLesson() throws Exception{
        Lesson lesson = new Lesson();
        lesson.setId(1);

        mockMvc.perform(delete("/api/v1/admin/delete-lesson/{lesson_id}",lesson.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(lessonService,times(1)).deleteLesson(lesson.getId());
    }

    @Test
    void deleteChild() throws Exception{
        Child child = new Child();
        child.setId(1);

        mockMvc.perform(delete("/api/v1/admin/delete-child/{child_id}",child.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(adminService,times(1)).deleteChild(child.getId());
    }

//    @Test
//    void deleteTeacher() throws Exception{
//        User teacher = new User();
//        teacher.setId(1);
//
//        mockMvc.perform(delete("/api/v1/admin/delete-teacher/{teacher_id}",teacher.getId())
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        verify(adminService,times(1)).deleteTeacher(teacher.getId());
//    }

    @Test
    void deleteGroup() throws Exception{
        Group group = new Group();
        group.setId(1);

        mockMvc.perform(delete("/api/v1/admin/delete-group/{app_group_id}",group.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(adminService,times(1)).deleteGroup(group.getId());
    }
    @Test
    void deleteSubject() throws Exception{
        Subject subject = new Subject();
        subject.setId(1);

        mockMvc.perform(delete("/api/v1/admin/delete-subject/{subject_id}",subject.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

        verify(adminService,times(1)).deleteSubject(subject.getId());
    }
}