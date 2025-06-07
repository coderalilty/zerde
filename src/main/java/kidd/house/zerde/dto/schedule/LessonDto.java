package kidd.house.zerde.dto.schedule;

import java.util.List;

public record LessonDto (TeacherDto teacherName, List<ChildDto> children,
                         String from, String to,
                         RoomDto roomDto
) {
}
