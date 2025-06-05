package kr.hhplus.ecommerce.infra.order;

import kr.hhplus.ecommerce.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(OrderEvent.OrderComplete event) {
        try {
            kafkaTemplate.send("order.v1.completed", event);
            log.info("주문완료 Kafka 발행 - orderId: {}, userId: {}, amount: {}", 
                    event.orderId(), event.userId(), event.paymentAmount());
        } catch (Exception e) {
            log.error("Kafka 발행 실패 - orderId: {}, error: {}", event.orderId(), e.getMessage());
        }
    }
}
