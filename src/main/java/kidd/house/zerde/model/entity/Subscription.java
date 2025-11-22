package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "remaining_lessons")
    private int remainingLessons;   // қанша сабақ қалды
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;

    private String status;         // подписка активті ма
    @Column(name = "price_paid")
    private Integer pricePaid;
    @ManyToOne
    @JoinColumn(name = "children_id",referencedColumnName = "id")
    private Child child;             // баланың аккаунты

    @ManyToOne
    @JoinColumn(name = "subscription_plans_id",referencedColumnName = "id")
    private SubscriptionPlan plan;  // қандай пакет таңдады
}
