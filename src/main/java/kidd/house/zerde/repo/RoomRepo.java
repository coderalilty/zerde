package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepo extends JpaRepository<Room,Integer> {
}
