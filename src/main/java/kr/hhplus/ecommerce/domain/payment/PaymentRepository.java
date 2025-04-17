package kr.hhplus.ecommerce.domain.payment;


import kr.hhplus.ecommerce.domain.payment.entity.Payment;

import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(Long paymentId);

    Optional<Payment> findByOrderId(Long orderId);

}
