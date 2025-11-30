package kidd.house.zerde.dto.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SubscriptionDto(
        int id,
        int remainingLessons,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String status,
        Integer pricePaid,
        Integer childId,
        Integer planId
) {}
