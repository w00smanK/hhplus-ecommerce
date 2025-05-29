package kr.hhplus.ecommerce.infra.order;

import kr.hhplus.ecommerce.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final int MAX_RETRIES = 4;
    private static final long RETRY_DELAY_MS = 1000;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(OrderEvent.OrderComplete event) {
        publishWithRetry(event, 0);
    }

    private void publishWithRetry(OrderEvent.OrderComplete event, int attempt) {
        try {
            kafkaTemplate.send("order.v1.completed", event);
        } catch (Exception e) {
            if (attempt < MAX_RETRIES) {
                int nextAttempt = attempt + 1;
                scheduler.schedule(() -> publishWithRetry(event, nextAttempt),
                        RETRY_DELAY_MS, TimeUnit.MILLISECONDS);
            } else {
                log.error("Kafka 발행 최종 실패 - orderId: {}, 총 {}회 시도 후 포기",
                        event.orderId(), MAX_RETRIES, e);
            }
        }
    }
}
