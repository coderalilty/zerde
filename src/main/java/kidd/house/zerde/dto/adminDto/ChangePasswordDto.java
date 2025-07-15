package kidd.house.zerde.dto.adminDto;

public record ChangePasswordDto(
        String email,
        String oldPassword,
        String newPassword
) {
}
