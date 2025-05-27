package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildRepo extends JpaRepository<Child,Long> {
}
