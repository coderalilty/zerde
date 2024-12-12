package kidd.house.zerde.dto.signupLesson;

import kidd.house.zerde.model.record.LessonDay;

public record SignUpLessonResponse(LessonDay lessonDay,
                                   LessonTypeDto lessonTypeDto) {
}
