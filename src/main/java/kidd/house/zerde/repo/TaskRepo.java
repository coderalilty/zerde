package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepo extends JpaRepository<Task,Integer> {
    Task findById(int taskId);
}
