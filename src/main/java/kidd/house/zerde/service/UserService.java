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

    //UserProfileDto getUserProfiles();

    UserProfileDto getUserProfiles(int user_id);

    void editUser(int user_id, EditUserDto editUserDto);

    List<Subscription> getUserSubscriptions(Integer user_id);

    List<Lesson> getTrialLessons(int user_id);

    List<Lesson> getPermanentLessons(int user_id);

    @Transactional
    KaspiPaymentResponseDto purchase(PurchaseSubscriptionDto dto);
}