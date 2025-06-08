package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepo extends JpaRepository<Group,Integer> {
    Group findById(int groupId);
}
