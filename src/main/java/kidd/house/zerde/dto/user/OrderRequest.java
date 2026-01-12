package kidd.house.zerde.dto.user;

public record OrderRequest(
        Integer orderId,
        Integer amount
) {
}
