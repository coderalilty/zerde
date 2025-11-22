package kidd.house.zerde.service;

import kidd.house.zerde.dto.payments.PurchaseSubscriptionDto;
import kidd.house.zerde.model.entity.Subscription;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();


    Subscription purchase(PurchaseSubscriptionDto dto);
}