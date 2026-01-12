package kidd.house.zerde.service.impl;

import kidd.house.zerde.config.FreedomPayConfig;
import kidd.house.zerde.service.FreedomPayService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FreedomPayServiceImpl implements FreedomPayService {
    private final FreedomPayConfig config;
    private final RestTemplate restTemplate = new RestTemplate();
    @Override
    public String createPayment(Integer orderId, Integer amount, String description) {
        // 1. Собираем параметры
        Map<String, String> params = new TreeMap<>(); // TreeMap сразу сортирует ключи по алфавиту
        params.put("pg_merchant_id", config.getMerchantId());
        params.put("pg_amount", amount.toString());
        params.put("pg_description", description);
        params.put("pg_order_id", orderId.toString());
        params.put("pg_salt", UUID.randomUUID().toString().substring(0, 8));

        // 2. Генерируем подпись
        String sig = generateSignature("init_payment.php", params);
        params.put("pg_sig", sig);

        // 3. Отправляем POST запрос (в формате Form Data)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        params.forEach(map::add);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // Получаем XML ответ
        String responseXml = restTemplate.postForObject(config.getApiUrl(), request, String.class);

        // Здесь нужно извлечь <pg_redirect_url> из XML.
        // Для простоты можно использовать Regex или библиотеку Jackson XML
        return extractRedirectUrl(responseXml);
    }
    private String generateSignature(String scriptName, Map<String, String> params) {
        // Алгоритм: script;val1;val2;...;secret
        String joinedValues = String.join(";", params.values());
        String baseString = scriptName + ";" + joinedValues + ";" + config.getSecretKey();

        return DigestUtils.md5Hex(baseString); // Используйте Apache Commons Codec
    }

    private String extractRedirectUrl(String xml) {
        // Простой парсинг для примера (лучше использовать XML Parser)
        return xml.substring(xml.indexOf("<pg_redirect_url>") + 17, xml.indexOf("</pg_redirect_url>"));
    }
}
