package kidd.house.zerde.service;

import kidd.house.zerde.dto.sendNotification.EmailMessageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class EmailKafkaProducerTest {
    @InjectMocks
    private EmailKafkaProducer emailKafkaProducer;
    @Mock
    private KafkaTemplate<Integer, EmailMessageDto> kafkaTemplate;

    @Test
    void sendEmail() {
        EmailMessageDto messageDto = new EmailMessageDto("10:00","geometry","text");
        emailKafkaProducer.sendEmail(messageDto);
        Mockito.verify(kafkaTemplate,Mockito.times(1)).send("emailMessageTopic",messageDto);

    }
}