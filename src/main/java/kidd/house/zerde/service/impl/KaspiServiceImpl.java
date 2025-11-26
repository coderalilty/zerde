package kidd.house.zerde.service.impl;

import kidd.house.zerde.dto.user.KaspiPaymentResponseDto;
import kidd.house.zerde.service.KaspiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class KaspiServiceImpl implements KaspiService {
    @Value("${kaspi.api.url}")
    private String kaspiApiUrl;

    @Value("${kaspi.merchant.id}")
    private String merchantId;

    @Value("${kaspi.secret.key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Создаёт платёж в Kaspi и возвращает данные для клиента (paymentId, redirect URL или QR).
     * Поля и путь зависят от документации. Здесь примерный JSON POST.
     */
    @Override
    public KaspiPaymentResponseDto createPayment(Integer amount, Integer localOrderId, String description) {
        String url = kaspiApiUrl + "/v2/payments/create"; // пример — замени на реальный

        Map<String, Object> payload = new HashMap<>();
        payload.put("merchantId", merchantId);
        payload.put("orderId", localOrderId.toString());
        payload.put("amount", amount); // проверь, в тенге или в тiын (копейки)
        payload.put("description", description);

        String signature = generateSignature(payload);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Kaspi-Signature", signature);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> resp = restTemplate.postForEntity(url, request, Map.class);

        if (resp.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Kaspi create payment failed: " + resp.getStatusCode());
        }

        Map body = resp.getBody();
        // Пример извлечения полей; подмени ключи на те, что возвращает твой Kaspi
        String paymentId = body.get("paymentId") != null ? body.get("paymentId").toString() : null;
        String status = body.get("status") != null ? body.get("status").toString() : null;
        String redirectUrl = body.get("redirectUrl") != null ? body.get("redirectUrl").toString() : null;
        String qrImageBase64 = body.get("qr") != null ? body.get("qr").toString() : null;

        return new KaspiPaymentResponseDto(paymentId, status, redirectUrl, qrImageBase64);
    }

    /**
     * Проверяет подпись callback'а от Kaspi. Формула подписи зависит от документации.
     * Здесь показан HMAC-SHA256 от JSON-полей (в простейшем виде).
     */
    @Override
    public boolean verifyCallbackSignature(String payloadString, String receivedSignature) {
        // Если Kaspi отдаёт подпись в base64/hmacSHA256 — надо реализовать конкретно.
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(payloadString.getBytes("UTF-8"));
            String calcSig = Base64.getEncoder().encodeToString(hash);
            return calcSig.equals(receivedSignature);
        } catch (Exception e) {
            return false;
        }
    }
    private String generateSignature(Map<String, Object> payload) {
        // VERY IMPORTANT: заменить логику на ту, что дана в Kaspi docs.
        // Ниже — простая HMAC-SHA256 от конкатенации значений:
        try {
            StringBuilder sb = new StringBuilder();
            payload.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> sb.append(e.getKey()).append("=").append(e.getValue()).append("&"));
            String toSign = sb.toString();
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(toSign.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating Kaspi signature", e);
        }
    }

    /**
     * Если нужно — запрос статуса платежа по paymentId
     */
    public Map<String, Object> getPaymentStatus(String paymentId) {
        String url = kaspiApiUrl + "/v2/payments/" + paymentId + "/status";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Kaspi-Merchant", merchantId);
        String signature = generateSignature(Map.of("paymentId", paymentId));
        headers.set("X-Kaspi-Signature", signature);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
        return resp.getBody();
    }
}
