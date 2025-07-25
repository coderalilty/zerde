package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepo extends JpaRepository<Subject,Integer> {
    Subject findById(int subjectId);
}
