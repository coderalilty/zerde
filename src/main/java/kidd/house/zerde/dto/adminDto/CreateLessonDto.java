package kidd.house.zerde.dto.adminDto;

public record CreateLessonDto(
   String createLessonFrom, String createLessonTo,
   String groupType, int groupId, int subjectId, int roomId, int teacherId
) {
}
