package kidd.house.zerde.dto.adminDto;

public record CreateLessonDto(
   String createLessonFrom, String createLessonTo,
   String groupType, int groupId, int subjectId, int roomId, int teacherId,
   String childName, String childSurName, String childLastName, int childAge,
   String parentName, String parentSurName, String parentLastName, String parentPhone, String parentEmail
) {
}
