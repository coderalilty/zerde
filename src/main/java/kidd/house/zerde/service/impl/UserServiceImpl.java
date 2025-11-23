package kidd.house.zerde.service.impl;

import kidd.house.zerde.dto.schedule.ChildDto;
import kidd.house.zerde.dto.schedule.ParentDto;
import kidd.house.zerde.dto.user.EditUserDto;
import kidd.house.zerde.dto.user.UserProfileDto;
import kidd.house.zerde.model.entity.User;
import kidd.house.zerde.repo.UserRepo;
import kidd.house.zerde.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
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




}




