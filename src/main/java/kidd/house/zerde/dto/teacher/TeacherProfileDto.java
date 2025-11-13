package kidd.house.zerde.dto.teacher;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record TeacherProfileDto(

        int id, String name, String surName, String lastName, String email,
        Collection<? extends GrantedAuthority> authorities) {
}
