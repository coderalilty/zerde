package kidd.house.zerde.service.impl;

import kidd.house.zerde.dto.registration.SignUpRequest;
import kidd.house.zerde.model.entity.User;
import kidd.house.zerde.model.role.Authorities;
import kidd.house.zerde.repo.UserRepo;
import kidd.house.zerde.service.JWTService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTService jwtService;

    @Test
    void signUp() {
        SignUpRequest signUpRequest = new SignUpRequest("ela","ela@gmail.com","123");
        User user = new User();
        user.setEmail(signUpRequest.email());
        user.setName(signUpRequest.name());
        user.setAuthorities(Authorities.USER);
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));
        authenticationService.signUp(signUpRequest);
        Mockito.verify(userRepo,Mockito.times(1)).save(user);
    }

    @Test
    void signIn() {
        SignUpRequest signUpRequest = new SignUpRequest("ela","el@gmail.com","123");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signUpRequest.email(),signUpRequest.password()));
       // Mockito.when(userRepo.findByEmail(signUpRequest.email())).thenReturn(signUpRequest.email());
//        Mockito.verify(userRepo,Mockito.times(1)).findByEmail(signUpRequest.email())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
    }

    @Test
    void refreshToken() {
    }
}