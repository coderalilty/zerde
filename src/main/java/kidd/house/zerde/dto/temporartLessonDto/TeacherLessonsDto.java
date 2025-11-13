package kidd.house.zerde.dto.temporartLessonDto;

import kidd.house.zerde.model.status.LessonStatus;

public record TeacherLessonsDto(
        String lessonDay,
        String lessonFrom,
        String lessonTo,
        LessonStatus lessonStatus,
        String groupType
) {
}
