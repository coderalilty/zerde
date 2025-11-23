package kidd.house.zerde.service;

import kidd.house.zerde.dto.user.EditUserDto;
import kidd.house.zerde.dto.user.UserProfileDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();
    UserProfileDto getUserProfiles();
    void editUser(int userId, EditUserDto editUserDto);
}