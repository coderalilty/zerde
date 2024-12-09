package kidd.house.zerde.dto;

import kidd.house.zerde.model.status.LessonStatus;
import kidd.house.zerde.model.type.GroupType;
import kidd.house.zerde.model.type.LessonType;

import java.time.LocalDateTime;
import java.util.List;

public record LessonDto (int lessonId, LessonType lessonType,
                         LessonStatus lessonStatus, LocalDateTime from,
                         LocalDateTime to, GroupType groupType,
                         SubjectDto subjectDto, RoomDto roomDto,
                         TeacherDto teacherDto, List<ChildDto> childDtos) {
}
