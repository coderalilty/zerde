package kidd.house.zerde.dto.user;

import kidd.house.zerde.dto.schedule.LessonMark;
import kidd.house.zerde.dto.schedule.LessonMark2;
import kidd.house.zerde.model.status.LessonStatus;
import kidd.house.zerde.model.type.LessonType;

import java.time.LocalDate;

public record LessonDto(
        int id,
        String lessonName,
        String lessonDay,
        LessonType lessonType,
        LessonStatus lessonStatus,
        String groupType,
        LessonMark lessonMark,
        LessonMark2 lessonMark2,
        Integer subjectId,
        Integer roomId,
        Integer userId,
        Integer groupId
) {}
