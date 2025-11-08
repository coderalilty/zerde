package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.LockedSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LockedSlotRepo extends JpaRepository<LockedSlot,Integer> {
    @Query("SELECT s FROM LockedSlot s " +
            "WHERE s.roomName = :roomName " +
            "AND s.lessonDay = :lockLessonDay " +
            "AND s.lockedFrom < :lockedTo " +
            "AND s.lockedTo > :lockedFrom")
    List<LockedSlot> findLockedBetween(
            @Param("lockLessonDay") String lockLessonDay,
            @Param("lockedFrom") String lockedFrom,
            @Param("lockedTo") String lockedTo,
            @Param("roomName") String roomName
    );
    @Query("""
        SELECT l FROM LockedSlot l 
        WHERE MONTH(STR_TO_DATE(l.lessonDay, '%Y-%m-%d')) = :month 
          AND YEAR(STR_TO_DATE(l.lessonDay, '%Y-%m-%d')) = :year
          AND l.roomName = :roomName
    """)
    List<LockedSlot> findByYearAndMonthAndRoom(
            @Param("year") int year,
            @Param("month") int month,
            @Param("roomName") String roomName
    );
}
