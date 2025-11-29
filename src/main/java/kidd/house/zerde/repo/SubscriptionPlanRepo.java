package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlanRepo extends JpaRepository<SubscriptionPlan,Integer> {
    SubscriptionPlan findByCode(String s);
    SubscriptionPlan findById(int subscriptionPlanId);
}
