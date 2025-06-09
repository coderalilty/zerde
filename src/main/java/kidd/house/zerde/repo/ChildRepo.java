package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildRepo extends JpaRepository<Child,Integer> {
    List<Child> findByLessonId(Long lessonId);
}
