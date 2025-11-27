package kidd.house.zerde.dto.user;
import java.time.LocalDateTime;

public record TrialLessonDto(
        int id,                // сабақтың ID
        String title,          // сабақ атауы
        String description,    // сабақ сипаттамасы
        LocalDateTime trialDate, // сабақ уақыты
        boolean active         // пробный сабақ белсенді ме
) {}
