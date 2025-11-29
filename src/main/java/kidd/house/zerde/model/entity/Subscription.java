package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "remaining_lessons")
    private int remainingLessons;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    // подписка активті ма
    private String status;
    @Column(name = "price_paid")
    private Integer pricePaid;
    @ManyToOne
    @JoinColumn(name = "children_id",referencedColumnName = "id")
    private Child child;
    @ManyToOne
    @JoinColumn(name = "subscription_plans_id",referencedColumnName = "id")
    private SubscriptionPlan plan;
}
