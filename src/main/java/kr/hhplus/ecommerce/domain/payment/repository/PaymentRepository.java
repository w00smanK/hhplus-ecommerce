package kr.hhplus.ecommerce.domain.payment.repository;

import kr.hhplus.ecommerce.domain.payment.entity.Payment;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findById(Long paymentId);
}
