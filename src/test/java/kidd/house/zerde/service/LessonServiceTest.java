package kidd.house.zerde.service;

import kidd.house.zerde.mapper.LessonMapper;
import kidd.house.zerde.repo.LessonRepo;
import kidd.house.zerde.repo.LockedSlotRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
class LessonServiceTest {
    @InjectMocks
    private LessonService lessonService;
    @Mock
    private LessonRepo lessonRepo;
    @Mock
    private LessonMapper lessonMapper;
    @Mock
    private LockedSlotRepo lockedSlotRepo;

    @Test
    void getAllLessons() {
        lessonService.getAllLessons();
        Mockito.verify(lessonRepo, Mockito.times(1)).findAll();
    }

    @Test
    void findById() {
        lessonService.findById(1);
        Mockito.verify(lessonRepo, Mockito.times(1)).findById(1);
    }

    @Test
    void findLessonsBetween() {
//        lessonService.findLessonsBetween("10:00","10:30","202");
//        Mockito.verify(lessonRepo.findByFromAndToAndRoom_Name("10:00","10:30","202"));202
    }

    @Test
    void lockLesson() {
    }
}