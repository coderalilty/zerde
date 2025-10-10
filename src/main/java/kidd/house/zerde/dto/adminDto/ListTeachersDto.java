package kidd.house.zerde.dto.adminDto;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public record ListTeachersDto(
        String name,
        String surName,
        String lastName,
        String email,
        GrantedAuthority authorities,
        boolean passwordTemporary,
        List<ListSubjectsDto> subjects
) {
}
