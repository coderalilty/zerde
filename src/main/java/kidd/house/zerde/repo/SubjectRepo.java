package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepo extends JpaRepository<Subject,Integer> {
    Subject findById(int subjectId);

    Subject findByName(String subjectName);

    List<Subject> findAllByUsersId(int userId);
}
