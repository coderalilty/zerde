package kidd.house.zerde.service;

import kidd.house.zerde.dto.adminDto.LessonDtos;
import kidd.house.zerde.dto.adminDto.LockLessonDto;
import kidd.house.zerde.dto.temporartLessonDto.TemporaryLessonDto;
import kidd.house.zerde.model.entity.Child;
import kidd.house.zerde.model.entity.Lesson;
import kidd.house.zerde.model.entity.LockedSlot;
import kidd.house.zerde.model.status.LessonStatus;
import kidd.house.zerde.model.type.LessonType;
import kidd.house.zerde.repo.LessonRepo;
import kidd.house.zerde.repo.LockedSlotRepo;
import kidd.house.zerde.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {
    @Autowired
    private LessonRepo lessonRepo;
    @Autowired
    private LockedSlotRepo lockedSlotRepo;
    @Autowired
    private UserRepo userRepo;
    public Lesson findById(int lessonId) {
        return lessonRepo.findById(lessonId);
    }
    public List<Lesson> findLessonsBetween(String lessonDay,String from,String to,String roomName){
        return lessonRepo.findByLessonDayAndFromAndToAndRoom_Name(lessonDay,from,to,roomName);
    }
    public void lockLesson(String lockLessonDay,String lockDateTimeFrom, String lockDateTimeTo, String roomName) {
        List<Lesson> existingLessons = findLessonsBetween(
                lockLessonDay, lockDateTimeFrom, lockDateTimeTo, roomName);

        if (!existingLessons.isEmpty()) {
            throw new IllegalStateException("В указанное время уже есть уроки");
        }

        // 2. Проверка: нет ли уже заглушки
        List<LockedSlot> lockedSlots = lockedSlotRepo
                .findLockedBetween(lockLessonDay,lockDateTimeFrom, lockDateTimeTo, roomName);

        if (!lockedSlots.isEmpty()) {
            throw new IllegalStateException("Уже стоит заглушка на это время");
        }

        // 3. Сохранение заглушки
        LockedSlot slot = new LockedSlot();
        slot.setLessonDay(lockLessonDay);
        slot.setLockedFrom(lockDateTimeFrom);
        slot.setLockedTo(lockDateTimeTo);
        slot.setRoomName(roomName);
        lockedSlotRepo.save(slot);
    }

    public List<LockLessonDto> getLockLesson() {
        List<LockedSlot> lockedLessons = lockedSlotRepo.findAll();
        return lockedLessons.stream()
                .map(this::toDtoLockedLessons)
                .toList();
    }

    private LockLessonDto toDtoLockedLessons(LockedSlot lockedSlot) {
        return new LockLessonDto(
                lockedSlot.getId(),
                lockedSlot.getLockedFrom(),
                lockedSlot.getLockedTo(),
                lockedSlot.getRoomName()
        );
    }

    public void deleteLockLesson(int lockLessonId) {
        lockedSlotRepo.deleteById(lockLessonId);
    }

    public void deleteLesson(int lessonId) {
        lessonRepo.deleteById(lessonId);
    }

    public void editLesson(int lessonId, LessonDtos lessonDtos) {
        Lesson lesson = lessonRepo.findById(lessonId);

        if (lessonDtos.lessonName() != null){
            lesson.setLessonName(lessonDtos.lessonName());
        }
        if (lessonDtos.lessonDay() != null) {
            lesson.setLessonDay(lessonDtos.lessonDay());
        }
        if (lessonDtos.from() != null){
            lesson.setFrom(lessonDtos.from());
        }
        if (lessonDtos.to() != null){
            lesson.setTo(lessonDtos.to());
        }
        if (lessonDtos.groupType() != null){
            lesson.setGroupType(lessonDtos.groupType());
        }
        if (lessonDtos.groupName() != null){
            lesson.getGroup().setName(lessonDtos.groupName());
        }
        if (lessonDtos.roomName() != null){
            lesson.getRoom().setName(lessonDtos.roomName());
        }
        if (lessonDtos.subjectName() != null){
            lesson.getSubject().setName(lessonDtos.subjectName());
        }
        lesson.setLessonStatus(LessonStatus.EDITED);

        lessonRepo.save(lesson);
    }

    public List<TemporaryLessonDto> getTrialLesson(int userId) {
        List<Lesson> trialLessons = lessonRepo.findAllByLessonTypeAndUserId(LessonType.TRIAL, userId);
        return trialLessons.stream()
                .map(this::mapToTemporaryLessonDto)
                .toList();
    }

    private TemporaryLessonDto mapToTemporaryLessonDto(Lesson lesson) {
        return new TemporaryLessonDto(
                lesson.getGroup().getChildren().stream()
                        .map(Child::getFirstName)
                        .toString(),
                lesson.getGroup().getChildren().stream()
                        .map(Child::getAge)
                        .toArray().length,
                lesson.getGroup().getChildren().stream()
                        .map(c -> c.getParent().getParentName())
                        .toString(),
                lesson.getGroup().getChildren().stream()
                        .map(c -> c.getParent().getParentPhone())
                        .toString(),
                lesson.getGroup().getChildren().stream()
                        .map(c -> c.getParent().getParentEmail())
                        .toString(),
                lesson.getLessonDay(),
                lesson.getFrom(),
                lesson.getTo()
        );
    }
}
