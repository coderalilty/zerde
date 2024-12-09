package kidd.house.zerde.controller;

import jakarta.validation.Valid;
import kidd.house.zerde.dto.JwtAuthenticationResponce;
import kidd.house.zerde.dto.RefreshTokenRequest;
import kidd.house.zerde.dto.SignInRequest;
import kidd.house.zerde.dto.SignUpRequest;
import kidd.house.zerde.model.entity.User;
import kidd.house.zerde.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest){
        try {
            // Проверяем наличие пользователя
            if (authenticationService.isUserExists(signUpRequest.name())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            }
            // Проверка, существует ли пользователь с таким email
            if (authenticationService.isEmailExists(signUpRequest.email())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
            }
            // Регистрация пользователя
            User newUser = authenticationService.signUp(signUpRequest);
            // Возвращаем информацию о созданном пользователе
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);

        } catch (Exception e) {
            // Логирование ошибки для последующей диагностики
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during signup");
        }
        //было до без обработки return ResponseEntity.ok(authenticationService.signUp(signUpRequest));
    }
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest){
        try {
            JwtAuthenticationResponce responce = authenticationService.signIn(signInRequest);

            if (responce == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            return ResponseEntity.ok(responce);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during signin");
        }
        //было до без обработки return ResponseEntity.ok(authenticationService.signIn(signInRequest));
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        try {
            JwtAuthenticationResponce jwtResponse = authenticationService.refreshToken(refreshTokenRequest);

            if (jwtResponse == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            return ResponseEntity.ok(jwtResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during token refresh");
        }
        //было до без обработки return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }
}
