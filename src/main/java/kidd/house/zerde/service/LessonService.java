package kidd.house.zerde.service;

import kidd.house.zerde.dto.schedule.*;
import kidd.house.zerde.mapper.LessonMapper;
import kidd.house.zerde.model.entity.Lesson;
import kidd.house.zerde.repo.LessonRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonService {
    @Autowired
    private LessonRepo lessonRepo;
    @Autowired
    private LessonMapper lessonMapper;
    public List<LessonDto> getAllLessons() {
        List<Lesson> lessons = lessonRepo.findAll();
        return lessonMapper.toDtoList(lessons);
    }
    public Optional<Lesson> findById(int lessonId) {
        return lessonRepo.findById(lessonId);
    }
}
