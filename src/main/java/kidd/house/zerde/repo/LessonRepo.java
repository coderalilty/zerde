package kidd.house.zerde.repo;

import kidd.house.zerde.dto.schedule.LessonDto;
import kidd.house.zerde.model.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepo extends JpaRepository<Lesson,Integer> {
    LessonDto findById(int lesson_id);
}
