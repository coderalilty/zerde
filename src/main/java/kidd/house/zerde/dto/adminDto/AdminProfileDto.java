package kidd.house.zerde.dto.adminDto;

import org.springframework.security.core.GrantedAuthority;

public record AdminProfileDto(
        int id,
        String name,
        String surName,
        String lastName,
        String email,
        GrantedAuthority authorities
) {
}
