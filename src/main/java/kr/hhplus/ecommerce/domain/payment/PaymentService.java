package kr.hhplus.ecommerce.domain.payment;


import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.domain.order.OrderRepository;
import kr.hhplus.ecommerce.domain.payment.dto.PaymentCommand;
import kr.hhplus.ecommerce.domain.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Payment save(PaymentCommand.Save command) {

        return paymentRepository.save(new Payment(command.orderId(), command.amount()));
    }

    @Transactional(readOnly = true)
    public Payment findPayment(PaymentCommand.FindOrder command) throws Exception {

        Payment payment = paymentRepository.findByOrderId(command.orderId())
                .orElseThrow(() -> new Exception(ErrorCode.NOT_FOUND.getMessage()));

        return payment;
    }

    @Transactional
    public Payment pay(PaymentCommand.Pay command) throws Exception {

        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new Exception(ErrorCode.NOT_FOUND.getMessage()));

        return payment.pay(command.paymentAmount());
    }
}
