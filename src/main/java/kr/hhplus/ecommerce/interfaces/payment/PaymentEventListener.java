package kr.hhplus.ecommerce.interfaces.payment;

import kr.hhplus.ecommerce.domain.order.OrderEvent;
import kr.hhplus.ecommerce.domain.payment.PaymentService;
import kr.hhplus.ecommerce.domain.payment.dto.PaymentCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentService paymentService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createPayment(OrderEvent.OrderConfirmed event) {
        log.info("결제 정보 생성 이벤트 수신 - orderId: {}, paymentAmount: {}", 
                event.orderId(), event.paymentAmount());
        
        paymentService.create(new PaymentCommand.Save(event.orderId(), event.paymentAmount()));
    }
}
