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
        //User teacher = lesson.getUser();
        //Room room = lesson.getRoom();
        Parent parent = lesson.getParent();

        TeacherDto teacherDto = new TeacherDto("Gregory");
        RoomDto roomDto = new RoomDto("202");
        ParentDto parentDto = new ParentDto(
                parent.getParentName(),
                parent.getParentPhone(),
                parent.getParentEmail());


        List<ChildDto> childDto = getChildFirstName(lesson);

        return new LessonDto(
                teacherDto,
                childDto,
                lesson.getFrom(),
                lesson.getTo(),
                roomDto,
                parentDto
        );
    }

    public List<ChildDto> getChildFirstName(Lesson lesson) {
        // 👇 Преобразуем всех детей в ChildDto
        List<ChildDto> childDto = lesson.getChildren().stream()
                .map(child -> new ChildDto(
                        child.getFirstName()
                ))
                .toList();
        return childDto;
    }
    public String getLessonTime(){
        List<Lesson> lesson = lessonRepo.findAll();
        String lessonTimeDto = lesson.get(0).getLessonTime();
        return lessonTimeDto;
    }

    public List<LessonDto> toDtoList(List<Lesson> lessons) {
        return lessons.stream()
                .map(this::toDto)
                .toList();
    }
}

