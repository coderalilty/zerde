package kidd.house.zerde.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
@ExtendWith(MockitoExtension.class)
class MailSenderServiceTest {
    @InjectMocks
    private MailSenderService mailSenderService;
    @Mock
    private JavaMailSender mailSender;

    @Test
    void send() {
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setFrom("elzat7@ya.ru");
//        mailMessage.setTo("mailTo");
//        mailMessage.setSubject("subject");
//        mailMessage.setText("message");
//        //Mockito.when(mailSender.send(Mockito.any())).thenReturn(mailMessage);
    }
}