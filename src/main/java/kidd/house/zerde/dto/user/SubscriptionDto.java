package kidd.house.zerde.dto.user;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SubscriptionDto(
        int id,
        String name,
        BigDecimal price,
        int durationInDays,
        LocalDate startDate,
        LocalDate endDate,
        boolean active
) {}
