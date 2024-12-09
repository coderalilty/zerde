package kidd.house.zerde.service;

import kidd.house.zerde.dto.JwtAuthenticationResponce;
import kidd.house.zerde.dto.RefreshTokenRequest;
import kidd.house.zerde.dto.SignInRequest;
import kidd.house.zerde.dto.SignUpRequest;
import kidd.house.zerde.model.entity.User;

public interface AuthenticationService {
    User signUp(SignUpRequest signUpRequest);
    JwtAuthenticationResponce signIn(SignInRequest signInRequest);
    JwtAuthenticationResponce refreshToken(RefreshTokenRequest refreshTokenRequest);
    boolean isUserExists(String name);
    boolean isEmailExists(String email);
}
