package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.domain.payment.dto.PaymentCommand;
import kr.hhplus.ecommerce.domain.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void pay(PaymentCommand.Payment command) {
        Payment payment = Payment.create( command.getOrderId(), command.getAmount());
        payment.pay();

        paymentRepository.save(payment);
    }

}
