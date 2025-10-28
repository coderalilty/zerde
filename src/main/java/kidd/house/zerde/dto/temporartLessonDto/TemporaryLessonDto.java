package kidd.house.zerde.dto.temporartLessonDto;

public record TemporaryLessonDto(
        String childName,
        int childAge,
        String parentName,
        String parentPhone,
        String parentEmail,
        String createTimeFrom,
        String createTimeTo
) {
}
