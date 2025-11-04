package kidd.house.zerde.dto.temporartLessonDto;

import java.util.List;

public record TemporaryLessonDto(
        String childName,
        List<Integer> childAge,
        String parentName,
        String parentPhone,
        String parentEmail,
        String createTimeFrom,
        String createTimeTo
) {
}
