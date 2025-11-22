package kidd.house.zerde.dto.payments;

public record CreateSubscriptionPlanDto(
       String code,
       String name,
       Integer total_lessons,
       Integer duration_days,
       Integer price,
       Boolean is_group
) {
}
