package kidd.house.zerde.service;

import kidd.house.zerde.dto.adminDto.ChangePasswordDto;
import kidd.house.zerde.dto.registration.JwtAuthenticationResponce;
import kidd.house.zerde.dto.registration.SignInRequest;

public interface AuthenticationService {
    JwtAuthenticationResponce signIn(SignInRequest signInRequest);

    void changePassword(ChangePasswordDto dto);
}
