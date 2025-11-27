package kidd.house.zerde.service;

import kidd.house.zerde.dto.user.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    UserDetailsService userDetailsService();
    UserProfileDto getUserProfiles();
    void editUser(int userId, EditUserDto editUserDto);
    SubscriptionDto getUserSubscription(int userId);
    List<LessonDto> getPermanentLessons(int userId);
    TrialLessonDto getTrialLesson(int userId);
    void buySubscription(int userId, int subscriptionPlanId );
}