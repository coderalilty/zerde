package kidd.house.zerde.controller;

import kidd.house.zerde.dto.teacher.*;
import kidd.house.zerde.dto.temporartLessonDto.TeacherLessonsDto;
import kidd.house.zerde.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class TeacherPanelController {
    private final TeacherService teacherService;
    @GetMapping("/my-profile/{user_id}")
    public ResponseEntity<TeacherProfileDto> getTeacherProfiles(@PathVariable int user_id){
        TeacherProfileDto teacherProfileDto = teacherService.getTeacherProfiles(user_id);
        return ResponseEntity.ok(teacherProfileDto);
    }
    @GetMapping("/teacher_subjects/{user_id}")
    public ResponseEntity<List<TeacherSubjectsDto>> getTeacherSubjects(
            @PathVariable int user_id,
            @RequestBody TeacherSubjectsDto teacherSubjects){
        List<TeacherSubjectsDto> teacherSubjectsDtos = teacherService.getTeacherSubjects(user_id,teacherSubjects);
        return ResponseEntity.ok(teacherSubjectsDtos);
    }
    @GetMapping("/teacher_lessons/{user_id}")
    public ResponseEntity<List<TeacherLessonsDto>> getTeacherLessons(
            @PathVariable int user_id,
            @RequestBody TeacherLessonsDto teacherLessonsDto
    ){
        List<TeacherLessonsDto> teacherLessonsDtos = teacherService.getTeacherLessons(user_id,teacherLessonsDto);
        return ResponseEntity.ok(teacherLessonsDtos);
    }
    @GetMapping("/lesson/{lesson_id}")
    public ResponseEntity<TeacherLessonsDto> getLesson(@PathVariable int lesson_id){
        TeacherLessonsDto teacherLessonsDto = teacherService.getLesson(lesson_id);
        return ResponseEntity.ok(teacherLessonsDto);
    }
    @PostMapping(value = "/create-task/{subject_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createTask(@PathVariable int subject_id,@ModelAttribute CreateTaskDto createTaskDto){
        teacherService.createTask(createTaskDto,subject_id);
        return new ResponseEntity<>("Create Task success!",HttpStatus.CREATED);
    }
    @PutMapping("/edit-my-profile/{user_id}")
    public ResponseEntity<String> editTeacher(@PathVariable int user_id,@RequestBody EditTeacherDto editTeacherDto){
        teacherService.editTeacher(user_id,editTeacherDto);
        return new ResponseEntity<>("Teacher edited", HttpStatus.OK);
    }
    @PutMapping("/edit-lesson/{lesson_id}")
    public ResponseEntity<String> editLesson(
            @PathVariable int lesson_id,
            @RequestBody TeacherEditLesson teacherEditLesson) throws IOException {
        teacherService.editLesson(lesson_id,teacherEditLesson);
        return new ResponseEntity<>("Lesson edited", HttpStatus.OK);
    }
    @PutMapping("/edit-task/{taskId}")
    public ResponseEntity<String> editTask(@PathVariable int taskId,@ModelAttribute CreateTaskDto createTaskDto){
        teacherService.editTask(taskId,createTaskDto);
        return new ResponseEntity<>("Task edited", HttpStatus.OK);
    }

    @DeleteMapping("/delete-task/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable int taskId){
        teacherService.deleteTask(taskId);
        return new ResponseEntity<>("Delete Task success!",HttpStatus.OK);
    }
    @DeleteMapping("/delete-document/{documentId}")
    public ResponseEntity<String> deleteDocument(@PathVariable int documentId){
        teacherService.deleteDocument(documentId);
        return new ResponseEntity<>("Delete Document success!",HttpStatus.OK);
    }
}
