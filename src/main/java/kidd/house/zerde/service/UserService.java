package kidd.house.zerde.service;

import kidd.house.zerde.dto.user.EditUserDto;
import kidd.house.zerde.dto.user.KaspiPaymentResponseDto;
import kidd.house.zerde.dto.user.PurchaseSubscriptionDto;
import kidd.house.zerde.dto.user.UserProfileDto;
import kidd.house.zerde.model.entity.Lesson;
import kidd.house.zerde.model.entity.Subscription;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    UserDetailsService userDetailsService();

    UserProfileDto getUserProfiles();

    void editUser(int userId, EditUserDto editUserDto);

    List<Subscription> getUserSubscriptions(Integer userId);

    List<Lesson> getTrialLessons(int userId);

    List<Lesson> getPermanentLessons(int userId);

    @Transactional
    KaspiPaymentResponseDto purchase(PurchaseSubscriptionDto dto);
}