package kidd.house.zerde.service;

import kidd.house.zerde.dto.sendNotification.EmailMessageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class EmailKafkaConsumerTest {
    @InjectMocks
    private EmailKafkaConsumer emailKafkaConsumer;
    @Mock
    private MailSenderService mailSenderService;

    @Test
    void listen() {
        EmailMessageDto emailMessageDto = new EmailMessageDto("10:00","geometry","text");

        emailKafkaConsumer.listen(emailMessageDto);

        Mockito.verify(mailSenderService,Mockito.times(1)).send(emailMessageDto.to(),emailMessageDto.subject(),emailMessageDto.text());
    }
}