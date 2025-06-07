package kidd.house.zerde.dto.sendNotification;

public record EmailMessageDto(
        String to,
        String subject,
        String text
) {}
