package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.LockedSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LockedSlotRepo extends JpaRepository<LockedSlot,Integer> {
    @Query("SELECT s FROM LockedSlot s WHERE s.roomName = :roomName AND s.lockedFrom < :to AND s.lockedTo > :from")
    List<LockedSlot> findLockedBetween(
            @Param("from") String lockedFrom,
            @Param("to") String lockedTo,
            @Param("roomName") String roomName);

    boolean existsByRoomNameAndLockedFromLessThanAndLockedToGreaterThan(
            String roomName,
            String lessonTo,
            String lessonFrom);
}
