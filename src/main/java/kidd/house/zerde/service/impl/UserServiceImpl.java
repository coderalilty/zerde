package kidd.house.zerde.service.impl;

import kidd.house.zerde.repo.UserRepo;
import kidd.house.zerde.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;

    public UserDetailsService userDetailsService(){
        return username -> userRepo.findByEmail(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
