package kidd.house.zerde.service.impl;

import kidd.house.zerde.dto.payments.PurchaseSubscriptionDto;
import kidd.house.zerde.dto.user.KaspiPaymentResponseDto;
import kidd.house.zerde.model.entity.Child;
import kidd.house.zerde.model.entity.Payment;
import kidd.house.zerde.model.entity.Subscription;
import kidd.house.zerde.model.entity.SubscriptionPlan;
import kidd.house.zerde.repo.*;
import kidd.house.zerde.service.KaspiService;
import kidd.house.zerde.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final SubscriptionPlanRepo subscriptionPlanRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final ChildRepo childRepo;
    private final PaymentRepo paymentRepo;
    private final KaspiService kaspiService;
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> userRepo.findByEmail(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


    @Override
    @Transactional
    public KaspiPaymentResponseDto purchase(PurchaseSubscriptionDto dto) {
        SubscriptionPlan plan = subscriptionPlanRepo.findByCode(dto.planCode());
        if (plan == null) throw new IllegalArgumentException("Plan not found: " + dto.planCode());

        int price = dto.pricePaid() == null ? plan.getPrice() : dto.pricePaid();

        // Создаем локальную запись платежа (PENDING)
        Payment payment = new Payment();
        payment.setKaspiStatus("PENDING");
        payment.setAmount(price);
        payment.setChildId(dto.childId());
        payment.setPlanCode(dto.planCode());
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepo.save(payment);

        // Создаем платёж в Kaspi
        KaspiPaymentResponseDto response = kaspiService.createPayment(
                price, payment.getId(), "Subscription " + dto.planCode()
        );

        // Сохраняем возвращенные данные от Kaspi
        payment.setKaspiPaymentId(response.kaspiPaymentId());
        payment.setKaspiStatus(response.status());
        payment.setRedirectUrl(response.redirectUrl());
        paymentRepo.save(payment);

        // возвращаем клиенту ссылку/QR
        return response;
    }
    /**
     * Этот метод вызывается из webhook при успешной оплате (status == SUCCESS).
     * Создаёт подписку на основе Payment.
     */
    @Override
    @Transactional
    public void finalizePaymentAndCreateSubscription(String kaspiPaymentId, String kaspiStatus) {
        Payment payment = paymentRepo.findByKaspiPaymentId(kaspiPaymentId);
        if (payment == null) throw new IllegalArgumentException("Payment not found: " + kaspiPaymentId);

        payment.setKaspiStatus(kaspiStatus);
        paymentRepo.save(payment);

        if (!"SUCCESS".equalsIgnoreCase(kaspiStatus)) {
            return;
        }

        // Создаём подписку только если ещё не создано (проверка по childId + planCode + незаконченная подписка — опционально)
        Subscription subscription = new Subscription();
        // если у тебя Child entity: загрузи её, тут для простоты используем childId
        Child child = childRepo.findById(payment.getChildId());
        subscription.setChild(child);
        SubscriptionPlan plan = subscriptionPlanRepo.findByCode(payment.getPlanCode());
        subscription.setPlan(plan);
        subscription.setRemainingLessons(plan.getTotalLessons());
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(plan.getDurationDays()));
        subscription.setStatus("ACTIVE");
        subscription.setPricePaid(payment.getAmount());

        subscriptionRepo.save(subscription);
    }


}
