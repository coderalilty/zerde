package kidd.house.zerde.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class FreedomPayConfig {
    @Value("${freedompay.merchant-id}")
    private String merchantId;

    @Value("${freedompay.secret-key}")
    private String secretKey;

    @Value("${freedompay.api-url}")
    private String apiUrl;
}
