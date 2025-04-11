package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Lesson;
import kidd.house.zerde.model.status.LessonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepo extends JpaRepository<Lesson,Integer> {
    Lesson findByLessonNameAndLessonDayAndLessonTime(
            String lessonName,
            String lessonDay,
            String lessonTime
    );
}
