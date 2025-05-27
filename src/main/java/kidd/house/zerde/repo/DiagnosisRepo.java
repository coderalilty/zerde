package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnosisRepo extends JpaRepository<Diagnosis,Long> {
}
