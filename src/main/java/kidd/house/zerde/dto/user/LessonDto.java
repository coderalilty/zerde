package kidd.house.zerde.dto.user;

import kidd.house.zerde.model.status.LessonStatus;
import kidd.house.zerde.model.type.LessonType;
public record LessonDto(
        int id,
        String lessonName,
        String lessonDay,
        LessonType lessonType,
        LessonStatus lessonStatus,
        Integer documentId,
        Integer subjectId,
        Integer roomId,
        Integer userId,
        Integer groupId
) {}



