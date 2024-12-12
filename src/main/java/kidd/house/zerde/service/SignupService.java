package kidd.house.zerde.service;

import kidd.house.zerde.dto.signupLesson.SignupRequestDto;
import org.springframework.stereotype.Service;

@Service
public class SignupService {
    public String saveSignup(SignupRequestDto signupRequest, String status) {
        // Логика сохранения записи с начальным статусом "draft"
        System.out.println("Сохранение записи с начальным статусом: " + status);
        return status;
    }

    public boolean verifySignup(SignupRequestDto signupRequest) {
        // Логика проверки верификации (например, проверка данных)
        return true; // Предположим, что верификация успешна
    }

    public String updateStatus(SignupRequestDto signupRequest, String newStatus) {
        // Логика обновления статуса заявки
        System.out.println("Статус заявки обновлен на: " + newStatus);
        return newStatus;
    }

    public void sendNotification(SignupRequestDto signupRequest) {
        // Логика отправки уведомления (в будущем можно интегрировать WhatsApp/Telegram API)
        System.out.println("Отправка уведомления для заявки: " + signupRequest.childName());
    }
}
