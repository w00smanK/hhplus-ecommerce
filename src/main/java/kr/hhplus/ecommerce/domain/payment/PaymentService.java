package kr.hhplus.ecommerce.domain.payment;


import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.config.exception.Exception;
import kr.hhplus.ecommerce.domain.payment.dto.PaymentCommand;
import kr.hhplus.ecommerce.domain.payment.entity.Payment;
import kr.hhplus.ecommerce.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment save(PaymentCommand.Save command) {

        Payment payment = Payment.builder()
                .orderId(command.orderId())
                .amount(command.amount())
                .build();

        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public Payment findPayment(PaymentCommand.Find command) {

        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new Exception(ErrorCode.NOT_FOUND));

        return payment;
    }

    @Transactional
    public Payment pay(PaymentCommand.Pay command) {

        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new Exception(ErrorCode.NOT_FOUND));

        return payment.pay(command.paymentAmount());
    }
}
