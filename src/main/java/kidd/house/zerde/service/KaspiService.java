package kidd.house.zerde.service;

import kidd.house.zerde.dto.user.KaspiPaymentResponseDto;

public interface KaspiService {
    boolean verifyCallbackSignature(String payloadString, String signature);

    KaspiPaymentResponseDto createPayment(Integer amount, Integer localOrderId, String description);
}
