package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.domain.payment.entity.Payment;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository {

    Payment save(Payment payment);

}
