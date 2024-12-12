package kidd.house.zerde.dto.schedule;

public record LessonDto (int lessonId, TeacherDto teacherName, ChildDto childDto,
                         String from, String to,
                         RoomDto roomDto, ParentDto parentDto
) {
}
//                         LessonType lessonType,
//                         LessonStatus lessonStatus,  GroupType groupType,
//                         SubjectDto subjectDto,
