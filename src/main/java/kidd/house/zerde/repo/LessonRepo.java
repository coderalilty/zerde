package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LessonRepo extends JpaRepository<Lesson,Integer> {
    Lesson findByLessonTimeAndFromAndTo(
            String From,
            String To,
            String LessonTime
    );
    Lesson findByFromAndTo(
            String From,
            String To
    );
}
