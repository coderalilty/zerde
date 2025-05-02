package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LessonRepo extends JpaRepository<Lesson,Integer> {
    Optional<Lesson> findByFromAndToAndLessonTime(
            String From,
            String To,
            String LessonTime
    );

    List<Lesson> findByFromAndToAndRoom_Name(
            String from,
            String to,
            String roomName);
}
