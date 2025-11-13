package kidd.house.zerde.service;

import kidd.house.zerde.dto.teacher.*;
import kidd.house.zerde.dto.temporartLessonDto.TeacherLessonsDto;

import java.io.IOException;
import java.util.List;

public interface TeacherService {
    TeacherProfileDto getTeacherProfiles(int user_id);

    List<TeacherSubjectsDto> getTeacherSubjects(int userId, TeacherSubjectsDto teacherSubjects);

    List<TeacherLessonsDto> getTeacherLessons(int userId, TeacherLessonsDto teacherLessonsDto);

    TeacherLessonsDto getLesson(int lessonId);

    void editLesson(int lessonId, TeacherEditLesson teacherEditLesson) throws IOException;

    void createTask(CreateTaskDto createTaskDto,int subject_id);

    void deleteTask(int taskId);

    void deleteDocument(int documentId);

    void editTask(int taskId, CreateTaskDto createTaskDto);

    void editTeacher(int userId, EditTeacherDto editTeacherDto);
}
