package kidd.house.zerde.service.impl;

import kidd.house.zerde.dto.registration.SignUpRequest;
import kidd.house.zerde.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @Test
    void signIn() {
        SignUpRequest signUpRequest = new SignUpRequest("ela","el@gmail.com","123");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signUpRequest.email(),signUpRequest.password()));
       // Mockito.when(userRepo.findByEmail(signUpRequest.email())).thenReturn(signUpRequest.email());
//        Mockito.verify(userRepo,Mockito.times(1)).findByEmail(signUpRequest.email())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
    }
}