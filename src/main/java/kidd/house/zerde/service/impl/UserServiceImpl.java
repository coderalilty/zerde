package kidd.house.zerde.service.impl;

import kidd.house.zerde.dto.schedule.ChildDto;
import kidd.house.zerde.dto.schedule.ParentDto;
import kidd.house.zerde.dto.user.*;
import kidd.house.zerde.model.entity.*;
import kidd.house.zerde.model.type.LessonType;
import kidd.house.zerde.repo.*;
import kidd.house.zerde.service.FreedomPayService;
import kidd.house.zerde.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final LessonRepo lessonRepo;
    private final SubscriptionPlanRepo subscriptionPlanRepo;
    private final PaymentRepo paymentRepo;
    private final ChildRepo childRepo;
    private final FreedomPayService freedomPayService;

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> userRepo.findByEmail(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    @Override
    public UserProfileDto getUserProfiles(int user_id) {

        User user = userRepo.findById(user_id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + user_id);
        }
        List<ChildDto> childrenDto = user.getChildren().stream()
                .map(child -> new ChildDto(
                        child.getFirstName(),
                        new ParentDto(
                                user.getName(),
                                user.getPhone(),
                                user.getEmail()
                        )
                )).toList();

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
    public void editUser(int user_id, EditUserDto editUserDto) {
        // 1. User-ды табу немесе жоқ болса қате шығару
        User user = userRepo.findById(user_id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + user_id);
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
    public List<SubscriptionDto> getUserSubscriptions(int user_id) {
        List<Subscription> subscriptions = subscriptionRepo.findAllByUserId(user_id);
        return subscriptions.stream()
                .map(this::toUserSubscriptionDto)
                .toList();
    }

    private SubscriptionDto toUserSubscriptionDto(Subscription subscription) {
        return new SubscriptionDto(
                subscription.getId(),
                subscription.getRemainingLessons(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getStatus(),
                subscription.getPricePaid(),
                subscription.getChild() != null ? subscription.getChild().getId() : null,
                subscription.getPlan() != null ? subscription.getPlan().getId() : null
        );
    }


    @Override
    public List<LessonDto> getTrialLessons(int user_id) {
        return lessonRepo.findAllByLessonTypeAndUserId(LessonType.TRIAL, user_id).stream()
                .map(this::toLessonDto)
                .toList();
    }

    private LessonDto toLessonDto(Lesson lesson) {
        return new LessonDto(
                lesson.getId(),
                lesson.getLessonName(),
                lesson.getLessonDay(),
                lesson.getLessonType(),
                lesson.getLessonStatus(),
                lesson.getGroupType(),
                lesson.getLessonMark(),
                lesson.getLessonMark2(),
                lesson.getSubject() != null ? lesson.getSubject().getId() : null,
                lesson.getRoom() != null ? lesson.getRoom().getId() : null,
                lesson.getUser() != null ? lesson.getUser().getId() : null,
                lesson.getGroup() != null ? lesson.getGroup().getId() : null
        );
    }

    @Override
    public List<LessonDto> getPermanentLessons(int user_id) {
        return lessonRepo.findAllByLessonTypeAndUserId(LessonType.PERMANENT, user_id).stream()
                .map(this::toLessonDto)
                .toList();
    }

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
