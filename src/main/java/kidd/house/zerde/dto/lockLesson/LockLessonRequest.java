package kidd.house.zerde.dto.lockLesson;

public record LockLessonRequest(
        String lockLessonDay,
        String lockDateTimeFrom,
        String lockDateTimeTo,
        String roomName
) {
}
