package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "kaspi_payment_id")
    private String kaspiPaymentId;   // id от Kaspi (paymentId)
    @Column(name = "kaspi_status")
    private String kaspiStatus;      // PENDING / SUCCESS / FAILED
    @Column(name = "redirect_url")
    private String redirectUrl;      // ссылка на оплату (или qr)
    private Integer amount;          // сумма в тг (или в тенге * 100, в зависимости от API)
    @Column(name = "child_id")
    private int childId;
    @Column(name = "plan_code")
    private String planCode;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
