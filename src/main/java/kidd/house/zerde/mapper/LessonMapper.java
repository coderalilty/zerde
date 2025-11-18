package kidd.house.zerde.mapper;


import kidd.house.zerde.dto.schedule.ChildDto;
import kidd.house.zerde.dto.schedule.ParentDto;
import kidd.house.zerde.model.entity.Lesson;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LessonMapper {
    public List<ChildDto> getChildFirstName(Lesson lesson) {
        // 👇 Преобразуем всех детей в ChildDto
        return lesson.getGroup().getChildren().stream()
                .map(child -> new ChildDto(
                        child.getFirstName(),
                        new ParentDto(
                                child.getUser().getName(),
                                child.getUser().getPhone(),
                                child.getUser().getEmail())
                ))
                .toList();
    }
}

