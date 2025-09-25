package kidd.house.zerde.dto.adminDto;

public record AdminProfileDto(
        int id,
        String name,
        String surName,
        String lastName,
        String email,
        java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> authorities
) {
}
