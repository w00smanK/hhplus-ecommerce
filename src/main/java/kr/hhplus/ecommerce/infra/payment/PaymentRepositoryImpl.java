package kr.hhplus.ecommerce.infra.payment;

import kr.hhplus.ecommerce.domain.payment.PaymentRepository;
import kr.hhplus.ecommerce.domain.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    @Override
    public Payment create(Payment payment) {
        return jpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(Long paymentId) {
        return jpaRepository.findById(paymentId);
    }

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId);
    }
}
