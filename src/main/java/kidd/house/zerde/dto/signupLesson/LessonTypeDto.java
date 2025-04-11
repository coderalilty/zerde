package kidd.house.zerde.dto.signupLesson;

import java.util.List;

public record LessonTypeDto(
        String lessonName,
        List<FreeLesson> freeLessons) {
}
