package kidd.house.zerde.service.impl;

import kidd.house.zerde.dto.schedule.ChildDto;
import kidd.house.zerde.dto.schedule.ParentDto;
import kidd.house.zerde.dto.user.EditUserDto;
import kidd.house.zerde.dto.user.KaspiPaymentResponseDto;
import kidd.house.zerde.dto.user.PurchaseSubscriptionDto;
import kidd.house.zerde.dto.user.UserProfileDto;
import kidd.house.zerde.model.entity.*;
import kidd.house.zerde.model.type.LessonType;
import kidd.house.zerde.repo.*;
import kidd.house.zerde.service.KaspiService;
import kidd.house.zerde.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final LessonRepo lessonRepo;
    private final SubscriptionPlanRepo subscriptionPlanRepo;
    private final KaspiService kaspiService;
    private final PaymentRepo paymentRepo;

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> userRepo.findByEmail(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    @Override
    public UserProfileDto getUserProfiles() {
        // Ағымдағы қолданушыны алу
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Children тізімін DTO-ға ауыстыру
        List<ChildDto> childrenDto = user.getChildren().stream()
                .map(child -> new ChildDto(
                        child.getFirstName(),
                        new ParentDto(
                                user.getName(),
                                user.getPhone(),
                                user.getEmail()
                        )
                ))
                .toList();

        return new UserProfileDto(
                user.getName(),
                user.getSurName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                childrenDto
        );
    }

    @Override
    public void editUser(int userId, EditUserDto editUserDto) {
        // 1. User-ды табу немесе жоқ болса қате шығару
        User user = userRepo.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        // 2. Тек DTO-дан мәні бар өрістерді жаңарту
        if (editUserDto.name() != null && !editUserDto.name().isBlank()) {
            user.setName(editUserDto.name());
        }
        if (editUserDto.surName() != null && !editUserDto.surName().isBlank()) {
            user.setSurName(editUserDto.surName());
        }
        if (editUserDto.lastName() != null && !editUserDto.lastName().isBlank()) {
            user.setLastName(editUserDto.lastName());
        }
        if (editUserDto.phone() != null && !editUserDto.phone().isBlank()) {
            user.setPhone(editUserDto.phone());
        }

        // 3. Өзгерістерді сақтау
        userRepo.save(user);
    }

    @Override
    public List<Subscription> getUserSubscriptions(Integer userId) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return subscriptionRepo.findAllByUserId(userId);
    }
    @Override
    public List<Lesson> getTrialLessons(int userId) {
        return lessonRepo.findAllByLessonTypeAndUserId(LessonType.TRIAL, userId);
    }

    @Override
    public List<Lesson> getPermanentLessons(int userId) {
        return lessonRepo.findAllByLessonTypeAndUserId(LessonType.PERMANENT, userId);
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
}
