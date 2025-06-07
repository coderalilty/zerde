package kidd.house.zerde.dto.signupLesson;

import java.util.List;

public record LessonTypeDto(
        String lessonName,
        String groupType,
        List<FreeLesson> freeLessons) {
}
