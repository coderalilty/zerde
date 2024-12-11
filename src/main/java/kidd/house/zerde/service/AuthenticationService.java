package kidd.house.zerde.service;

import kidd.house.zerde.dto.registration.JwtAuthenticationResponce;
import kidd.house.zerde.dto.registration.RefreshTokenRequest;
import kidd.house.zerde.dto.registration.SignInRequest;
import kidd.house.zerde.dto.registration.SignUpRequest;
import kidd.house.zerde.model.entity.User;

public interface AuthenticationService {
    User signUp(SignUpRequest signUpRequest);
    JwtAuthenticationResponce signIn(SignInRequest signInRequest);
    JwtAuthenticationResponce refreshToken(RefreshTokenRequest refreshTokenRequest);
//    boolean isUserExists(String name);
//    boolean isEmailExists(String email);
}
