package kidd.house.zerde.dto.user;

import kidd.house.zerde.dto.schedule.ChildDto;

import java.util.List;

public record UserProfileDto(
        String name,
        String surName,
        String lastName,
        String email,
        String phone,
        List<ChildDto> children
) {
}
