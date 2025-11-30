package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepo extends JpaRepository<Subscription,Integer> {
    @Query("SELECT s FROM Subscription s WHERE s.child.user.id = :userId")
    List<Subscription> findAllByUserId(@Param("userId") int userId);
}
