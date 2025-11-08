package kidd.house.zerde.dto.temporartLessonDto;

import java.util.List;

public record TemporaryLessonDtos(
        String childName,
        List<Integer> childAge,
        String parentName,
        String parentPhone,
        String parentEmail,
        String lessonDay,
        String createTimeFrom,
        String createTimeTo
) {
}
