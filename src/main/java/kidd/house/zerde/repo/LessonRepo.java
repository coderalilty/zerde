package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Lesson;
import kidd.house.zerde.model.type.LessonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LessonRepo extends JpaRepository<Lesson,Integer> {
    List<Lesson> findByLessonDayAndFromAndToAndRoom_Name(
            String lessonDay,
            String from,
            String to,
            String roomName);
    Lesson findById(int lessonId);
    List<Lesson> findAllByLessonType(LessonType lessonType);
    List<Lesson> findAllByLessonTypeAndUserId(LessonType lessonType, Integer userId);
}
