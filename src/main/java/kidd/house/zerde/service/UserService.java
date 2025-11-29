package kidd.house.zerde.service;

import kidd.house.zerde.dto.user.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    UserDetailsService userDetailsService();
    UserProfileDto getUserProfiles();
    void editUser(int userId, EditUserDto editUserDto);
}