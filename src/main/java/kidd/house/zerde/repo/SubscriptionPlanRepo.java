package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface SubscriptionPlanRepo extends JpaRepository<SubscriptionPlan,Integer> {
    Optional<SubscriptionPlan> findByName(String name);
}
