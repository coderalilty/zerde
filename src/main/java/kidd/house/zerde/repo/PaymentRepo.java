package kidd.house.zerde.repo;

import kidd.house.zerde.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<Payment,Integer> {
    Payment findByKaspiPaymentId(String kaspiPaymentId);
}
