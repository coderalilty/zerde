package kidd.house.zerde.service;

import kidd.house.zerde.dto.schedule.*;
import kidd.house.zerde.mapper.LessonMapper;
import kidd.house.zerde.model.entity.Lesson;
import kidd.house.zerde.model.entity.LockedSlot;
import kidd.house.zerde.repo.LessonRepo;
import kidd.house.zerde.repo.LockedSlotRepo;
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
    @Autowired
    private LockedSlotRepo lockedSlotRepo;
    public List<LessonDto> getAllLessons() {
        List<Lesson> lessons = lessonRepo.findAll();
        return lessonMapper.toDtoList(lessons);
    }
    public Optional<Lesson> findById(int lessonId) {
        return lessonRepo.findById(lessonId);
    }
    public List<Lesson> findLessonsBetween(String from,String to,String roomName){
        return lessonRepo.findByFromAndToAndRoom_Name(from,to,roomName);
    }
    public void lockLesson(String lockDateTimeFrom, String lockDateTimeTo, String roomName) {
        List<Lesson> existingLessons = findLessonsBetween(
                lockDateTimeFrom, lockDateTimeTo, roomName);

        if (!existingLessons.isEmpty()) {
            throw new IllegalStateException("В указанное время уже есть уроки");
        }

        // 2. Проверка: нет ли уже заглушки
        List<LockedSlot> lockedSlots = lockedSlotRepo
                .findLockedBetween(lockDateTimeFrom, lockDateTimeTo, roomName);

        if (!lockedSlots.isEmpty()) {
            throw new IllegalStateException("Уже стоит заглушка на это время");
        }

        // 3. Сохранение заглушки
        LockedSlot slot = new LockedSlot();
        slot.setLockedFrom(lockDateTimeFrom);
        slot.setLockedTo(lockDateTimeTo);
        slot.setRoomName(roomName);
        lockedSlotRepo.save(slot);
    }
}
