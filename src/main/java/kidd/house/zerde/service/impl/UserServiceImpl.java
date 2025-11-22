package kidd.house.zerde.service.impl;

import kidd.house.zerde.dto.payments.PurchaseSubscriptionDto;
import kidd.house.zerde.model.entity.Child;
import kidd.house.zerde.model.entity.Subscription;
import kidd.house.zerde.model.entity.SubscriptionPlan;
import kidd.house.zerde.repo.ChildRepo;
import kidd.house.zerde.repo.SubscriptionPlanRepo;
import kidd.house.zerde.repo.SubscriptionRepo;
import kidd.house.zerde.repo.UserRepo;
import kidd.house.zerde.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final SubscriptionPlanRepo subscriptionPlanRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final ChildRepo childRepo;
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> userRepo.findByEmail(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public Subscription purchase(PurchaseSubscriptionDto dto) {
        SubscriptionPlan plan = subscriptionPlanRepo.findByCode(dto.planCode());
        Child child = childRepo.findById(dto.childId());
        if (plan == null) {
            throw new IllegalArgumentException("Plan not found: " + dto.planCode());
        }

        Subscription subscription = Subscription.builder()
                .child(child)
                .plan(plan)
                .remainingLessons(plan.getTotalLessons())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(plan.getDurationDays()))
                .status("ACTIVE")
                .pricePaid(dto.pricePaid() == null ? plan.getPrice() : dto.pricePaid())
                .build();

        return subscriptionRepo.save(subscription);
    }


}
