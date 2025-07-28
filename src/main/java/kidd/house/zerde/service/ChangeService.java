package kidd.house.zerde.service;

import kidd.house.zerde.dto.adminDto.ChangePasswordDto;
import kidd.house.zerde.model.entity.User;
import kidd.house.zerde.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    public void changePassword(ChangePasswordDto dto) {
        User user = userRepo.findByEmail(dto.email())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        // Проверка: временный ли пароль
        if (!user.isPasswordTemporary()) {
            throw new IllegalStateException("Пароль уже был изменен.");
        }

        // Проверка старого пароля
        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Неверный текущий пароль.");
        }

        // Меняем пароль
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        user.setPasswordTemporary(false); // больше менять нельзя

        userRepo.save(user);
    }
}
