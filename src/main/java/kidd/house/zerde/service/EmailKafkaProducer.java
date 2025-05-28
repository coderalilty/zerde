package kidd.house.zerde.service;

import kidd.house.zerde.dto.sendNotification.EmailMessageDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailKafkaProducer {

    private final KafkaTemplate<Integer, EmailMessageDto> kafkaTemplate;

    public EmailKafkaProducer(KafkaTemplate<Integer, EmailMessageDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmail(EmailMessageDto messageDto) {
        kafkaTemplate.send("emailMessageTopic", messageDto);
    }
}
