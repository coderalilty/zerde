package kidd.house.zerde.dto.adminDto;

public record LessonDtos(
        String lessonName,
        String from,
        String to,
        String lessonDay,
        String groupType,
        String groupName,
        String roomName,
        String subjectName
) {
}
