package kidd.house.zerde.service;

import kidd.house.zerde.dto.payments.PurchaseSubscriptionDto;
import kidd.house.zerde.dto.user.KaspiPaymentResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();


    KaspiPaymentResponseDto purchase(PurchaseSubscriptionDto dto);

    void finalizePaymentAndCreateSubscription(String s, String status);
}