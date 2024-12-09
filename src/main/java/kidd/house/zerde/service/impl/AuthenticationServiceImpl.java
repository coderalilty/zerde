package kidd.house.zerde.service.impl;

import kidd.house.zerde.dto.JwtAuthenticationResponce;
import kidd.house.zerde.dto.RefreshTokenRequest;
import kidd.house.zerde.dto.SignInRequest;
import kidd.house.zerde.dto.SignUpRequest;
import kidd.house.zerde.model.entity.User;
import kidd.house.zerde.model.role.Authorities;
import kidd.house.zerde.repo.UserRepo;
import kidd.house.zerde.service.AuthenticationService;
import kidd.house.zerde.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    public boolean isUserExists(String name) {
        return userRepo.findByName(name).isPresent();
    }
    public boolean isEmailExists(String email) {
        // Проверяем наличие пользователя в базе данных по email
        return userRepo.findByEmail(email).isPresent();
    }
    public User signUp(SignUpRequest signUpRequest){
        User user = new User();
        user.setEmail(signUpRequest.email());
        user.setName(signUpRequest.name());
        user.setLogin(signUpRequest.login());
        user.setAuthorities(Authorities.TEACHER);
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));

        return userRepo.save(user);
    }
    public JwtAuthenticationResponce signIn(SignInRequest signInRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.email(),
                signInRequest.password()));
        var user = userRepo.findByEmail(signInRequest.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefrechToken(new HashMap<>(), user);

        JwtAuthenticationResponce jwtAuthenticationResponce
                = new JwtAuthenticationResponce(jwt,refreshToken);

        return jwtAuthenticationResponce;
        //        jwtAuthenticationResponce.token(jwt);
//        jwtAuthenticationResponce.refreshToken(jwt);
    }
    public JwtAuthenticationResponce refreshToken(RefreshTokenRequest refreshTokenRequest){
        String userEmail = jwtService.extractUserName(refreshTokenRequest.token());
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        if (jwtService.isTokenValid(refreshTokenRequest.token(),user)){
            var jwt = jwtService.generateToken(user);
            JwtAuthenticationResponce jwtAuthenticationResponce
                    = new JwtAuthenticationResponce(jwt,refreshTokenRequest.token());

            return jwtAuthenticationResponce;
        }
        return null;
    }
}
