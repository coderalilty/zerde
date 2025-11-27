package kidd.house.zerde.service.impl;

import kidd.house.zerde.dto.schedule.ChildDto;
import kidd.house.zerde.dto.schedule.ParentDto;
import kidd.house.zerde.dto.user.*;
import kidd.house.zerde.model.entity.Lesson;
import kidd.house.zerde.model.entity.SubscriptionPlan;
import kidd.house.zerde.model.entity.TrialLesson;
import kidd.house.zerde.model.entity.User;
import kidd.house.zerde.repo.LockedSlotRepo;
import kidd.house.zerde.repo.SubscriptionPlanRepo;
import kidd.house.zerde.repo.UserRepo;
import kidd.house.zerde.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final SubscriptionPlanRepo subscriptionPlanRepo;
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
    public SubscriptionDto getUserSubscription(int userId) {

        User user = userRepo.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        SubscriptionPlan plan = user.getSubscriptionPlan();

        if (plan == null) {
            return null;
        }

        boolean isActive = user.getSubscriptionEndDate() != null &&
                user.getSubscriptionEndDate().isAfter(LocalDate.now());

        return new SubscriptionDto(
                plan.getId(),
                plan.getName(),
                plan.getPrice(),
                plan.getDurationInDays(),
                user.getSubscriptionStartDate(),
                user.getSubscriptionEndDate(),
                isActive
        );
    }
    public List<LessonDto> getPermanentLessons(int userId) {
        User user = userRepo.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        List<Lesson> lessons = user.getPermanentLessons(); // User ентитиде List<Lesson> болуы керек

        return lessons.stream()
                .map(lesson -> new LessonDto(
                        lesson.getId(),
                        lesson.getLessonName(),
                        lesson.getLessonDay(),
                        lesson.getLessonType(),
                        lesson.getLessonStatus(),
                        lesson.getDocument() != null ? lesson.getDocument().getId() : null,
                        lesson.getSubject() != null ? lesson.getSubject().getId() : null,
                        lesson.getRoom() != null ? lesson.getRoom().getId() : null,
                        lesson.getUser() != null ? lesson.getUser().getId() : null,
                        lesson.getGroup() != null ? lesson.getGroup().getId() : null
                ))
                .collect(Collectors.toList()); // Міне, осы керек

    }

    @Override
    public TrialLessonDto getTrialLesson(int userId) {
       User user = userRepo.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        TrialLesson trialLesson = user.getTrialLesson(); // User entity-де TrialLesson объекті

        if (trialLesson == null) {
            return null; // пробный урок жоқ болса
        }

        return new TrialLessonDto(
                trialLesson.getId(),
                trialLesson.getTitle(),
                trialLesson.getDescription(),
                trialLesson.getTrialDate(),
                trialLesson.isActive()
        );
    }


    @Override
    public void buySubscription(int userId, int subscriptionPlanId) {
        User user = userRepo.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        SubscriptionPlan plan = subscriptionPlanRepo.findById(subscriptionPlanId)
                .orElseThrow(() -> new RuntimeException("SubscriptionPlan not found"));

        user.setSubscriptionPlan(plan);
        user.setSubscriptionStartDate(LocalDate.now());
        user.setSubscriptionEndDate(LocalDate.now().plusDays(plan.getDurationInDays()));

        userRepo.save(user); // өзгерісті сақтау
    }


}




