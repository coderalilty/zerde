package kidd.house.zerde.dto.teacher;

import kidd.house.zerde.dto.schedule.LessonMark;
import kidd.house.zerde.dto.schedule.LessonMark2;
import org.springframework.web.multipart.MultipartFile;

public record TeacherEditLesson(
        LessonMark lessonMark,
        LessonMark2 lessonMark2,
        String lessonPlanURL,
        String filePath,
        String documentName,
        MultipartFile file
) {
}
