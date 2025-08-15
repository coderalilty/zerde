package kidd.house.zerde.mapper;


import kidd.house.zerde.dto.schedule.*;
import kidd.house.zerde.model.entity.*;
import kidd.house.zerde.repo.LessonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LessonMapper {
    @Autowired
    private LessonRepo lessonRepo;
    public LessonDto toDto(Lesson lesson) {

        TeacherDto teacherDto = new TeacherDto("Gregory");
        RoomDto roomDto = new RoomDto("202");

        List<ChildDto> childDto = getChildFirstName(lesson);

        return new LessonDto(
                teacherDto,
                childDto,
                lesson.getFrom(),
                lesson.getTo(),
                roomDto
        );
    }


    public List<ChildDto> getChildFirstName(Lesson lesson) {
        // 👇 Преобразуем всех детей в ChildDto
        return lesson.getChildren().stream()
                .map(child -> new ChildDto(
                        child.getFirstName(),
                        new ParentDto(
                                child.getParent().getParentName(),
                                child.getParent().getParentPhone(),
                                child.getParent().getParentEmail())
                ))
                .toList();
    }
    public String getLessonTime(){
        List<Lesson> lesson = lessonRepo.findAll();
        return lesson.get(0).getLessonDay();
    }

    public List<LessonDto> toDtoList(List<Lesson> lessons) {
        return lessons.stream()
                .map(this::toDto)
                .toList();
    }
}

