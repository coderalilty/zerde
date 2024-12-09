package kidd.house.zerde.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignInRequest(
        @Email(message = "Email should be valid")String email,
        @NotBlank(message = "Password is required")String password) {
}
