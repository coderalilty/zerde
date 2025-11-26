package kidd.house.zerde.dto.user;

public record KaspiCallbackDto(
        String paymentId,
        String orderId,
        String status,
        String amount,
        String signature // если есть
) {
}
