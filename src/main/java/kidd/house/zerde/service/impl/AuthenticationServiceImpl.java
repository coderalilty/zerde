package kidd.house.zerde.service.impl;

import kidd.house.zerde.dto.registration.JwtAuthenticationResponce;
import kidd.house.zerde.dto.registration.RefreshTokenRequest;
import kidd.house.zerde.dto.registration.SignInRequest;
import kidd.house.zerde.dto.registration.SignUpRequest;
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

        return new JwtAuthenticationResponce(jwt,refreshToken);
    }
    public JwtAuthenticationResponce refreshToken(RefreshTokenRequest refreshTokenRequest){
        String userEmail = jwtService.extractUserName(refreshTokenRequest.token());
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        if (jwtService.isTokenValid(refreshTokenRequest.token(),user)){
            var jwt = jwtService.generateToken(user);

            return new JwtAuthenticationResponce(jwt,refreshTokenRequest.token());
        }
        return null;
    }
}
