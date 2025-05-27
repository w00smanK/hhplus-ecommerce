package kr.hhplus.ecommerce.domain.payment;


import kr.hhplus.ecommerce.config.exception.CustomException;
import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.domain.order.OrderRepository;
import kr.hhplus.ecommerce.domain.payment.dto.PaymentCommand;
import kr.hhplus.ecommerce.domain.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Payment create(PaymentCommand.Save command) {

        return paymentRepository.create(new Payment(command.orderId(), command.amount()));
    }

    @Transactional(readOnly = true)
    public Payment findPayment(PaymentCommand.FindOrder command) throws Exception {

        Payment payment = paymentRepository.findByOrderId(command.orderId())
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        return payment;
    }

    @Transactional
    public Payment pay(PaymentCommand.Pay command) throws Exception {

        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        return payment.pay(command.paymentAmount());
    }
}
