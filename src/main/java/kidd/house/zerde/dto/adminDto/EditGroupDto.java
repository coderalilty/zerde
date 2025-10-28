package kidd.house.zerde.dto.adminDto;

import java.util.List;

public record EditGroupDto(
        String groupName,
        List<Integer> childIds
) {
}
