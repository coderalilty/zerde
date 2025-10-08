package kidd.house.zerde.dto.adminDto;

import kidd.house.zerde.model.entity.Subject;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public record ListTeachersDto(
        String name,
        String surName,
        String lastName,
        String email,
        Collection<? extends GrantedAuthority> authorities,
        boolean passwordTemporary,
        List<Subject> subjects
) {
}
