package kidd.house.zerde.service;

import kidd.house.zerde.dto.user.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    UserDetailsService userDetailsService();

    //UserProfileDto getUserProfiles();

    UserProfileDto getUserProfiles(int user_id);

    void editUser(int user_id, EditUserDto editUserDto);

    List<SubscriptionDto> getUserSubscriptions(int user_id);
    List<LessonDto> getTrialLessons(int user_id);

    List<LessonDto> getPermanentLessons(int user_id);

    KaspiPaymentResponseDto purchase(PurchaseSubscriptionDto dto);

    void finalizePaymentAndCreateSubscription(String s, String status);
}