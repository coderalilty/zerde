package kidd.house.zerde.controller;

import jakarta.validation.Valid;
import kidd.house.zerde.dto.adminDto.ChangePasswordDto;
import kidd.house.zerde.dto.registration.JwtAuthenticationResponce;
import kidd.house.zerde.dto.registration.RefreshTokenRequest;
import kidd.house.zerde.dto.registration.SignInRequest;
import kidd.house.zerde.dto.registration.SignUpRequest;
import kidd.house.zerde.service.AuthenticationService;
import kidd.house.zerde.service.ChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final ChangeService changeService;
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest){
        authenticationService.signUp(signUpRequest);
        return ResponseEntity.ok("Аккаунт успешно сохранен!");
    }
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponce> signIn(@RequestBody SignInRequest signInRequest){
        return ResponseEntity.ok(authenticationService.signIn(signInRequest));
    }
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponce> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordDto dto
    ) {
        changeService.changePassword(dto.email(), dto);
        return ResponseEntity.ok("Пароль успешно изменен");
    }
}
