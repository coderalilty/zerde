package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LessonRepo extends JpaRepository<Lesson,Integer> {
    List<Lesson> findByFromAndToAndRoom_Name(
            String from,
            String to,
            String roomName);
}
