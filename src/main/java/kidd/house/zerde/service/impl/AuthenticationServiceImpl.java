package kidd.house.zerde.service.impl;

import kidd.house.zerde.dto.adminDto.ChangePasswordDto;
import kidd.house.zerde.dto.registration.JwtAuthenticationResponce;
import kidd.house.zerde.dto.registration.SignInRequest;
import kidd.house.zerde.model.entity.User;
import kidd.house.zerde.repo.UserRepo;
import kidd.house.zerde.service.AuthenticationService;
import kidd.house.zerde.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    public JwtAuthenticationResponce signIn(SignInRequest signInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.email(),
                signInRequest.password()));
        var user = userRepo.findByEmail(signInRequest.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (user.isPasswordTemporary()) {
            throw new IllegalArgumentException("Необходимо сменить временный пароль");
        }
        var jwt = jwtService.generateToken(user);

        return new JwtAuthenticationResponce(jwt);
    }

    @Override
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
