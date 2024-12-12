package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Information;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InformationRepo extends JpaRepository<Information,Integer> {
}
