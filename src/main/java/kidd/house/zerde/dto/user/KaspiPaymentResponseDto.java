package kidd.house.zerde.dto.user;

public record KaspiPaymentResponseDto(
        String kaspiPaymentId,
        String status,
        String redirectUrl,
        String qrImageBase64
) {
}
