package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "subscription_plans")
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String code;
    private String name;            // Жеке 10, Жеке 12, Группа коррекция и тд
    @Column(name = "total_lessons")
    private int totalLessons;       // 10, 12, 8
    @Column(name = "duration_days")
    private int durationDays;       // 30 күн
    private int price;              // 40000, 48000...
    @Column(name = "is_group")
    private boolean isGroup;        // true = группа, false = индивидуально
    @OneToMany(mappedBy = "plan")
    private List<Subscription> subscriptions;
}
