package kidd.house.zerde.dto.payments;

public record PurchaseSubscriptionDto(
        int childId,
        String planCode, // e.g. PERSONAL_10
        Integer pricePaid
) {
}
