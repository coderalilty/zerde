package kidd.house.zerde.dto.user;

public record PurchaseSubscriptionDto(
        int childId,
        String planCode,
        Integer pricePaid
) {
}
