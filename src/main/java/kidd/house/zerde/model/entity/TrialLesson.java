package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "trial_lessons")
public class TrialLesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    private String description;

    private LocalDateTime trialDate;

    private boolean active;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user; // қай User-ге тиесілі
}
