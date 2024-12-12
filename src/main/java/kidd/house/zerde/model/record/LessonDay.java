package kidd.house.zerde.model.record;


import kidd.house.zerde.dto.schedule.LessonDto;

import java.time.LocalDateTime;

public record LessonDay(String weekDayName, LocalDateTime weekDay, LessonDto lessonDto) {
}
