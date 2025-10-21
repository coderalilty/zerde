package kidd.house.zerde.dto.adminDto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record AdminProfileDto(
        int id,
        String name,
        String surName,
        String lastName,
        String email,
        Collection<? extends GrantedAuthority> authorities
) {
}
