package kidd.house.zerde.dto.weekSchedule;

import kidd.house.zerde.dto.schedule.LessonDto;
import kidd.house.zerde.dto.schedule.RoomDto;

import java.util.List;

public record WeekScheduleResponse(String lessonDate,
                                   List<RoomDto> rooms,
                                   List<LessonDto> lessons) {
}