package kidd.house.zerde.dto.adminDto;

public record LockLessonDto(
        int lesson_id,
        String from,
        String to,
        String room
) {
}
