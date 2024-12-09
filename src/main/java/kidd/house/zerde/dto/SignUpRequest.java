package kidd.house.zerde.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @NotBlank(message = "Name is required")String name,
        @NotBlank(message = "Login is required")String login,
        @Email(message = "Email should be valid")String email,
        @NotBlank(message = "Password is required")String password) {
}
