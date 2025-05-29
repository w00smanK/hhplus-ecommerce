package kr.hhplus.ecommerce.interfaces.order;

import kr.hhplus.ecommerce.domain.order.OrderEvent;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "order.v1.completed",
            groupId = "order-service",
            concurrency = "4"
    )
    public void handleOrderComplete(OrderEvent.OrderComplete event, Acknowledgment ack) {
        log.info("=== Kafka 주문 완료 이벤트 수신 ===");
        log.info("주문완료 - orderId: {}, userId: {}, amount: {}", event.orderId(), event.userId(), event.paymentAmount());
        try {
            orderService.sendOrder(OrderCommand.Send.of(event));
            // 성공적으로 처리되었으면 오프셋 수동 커밋
            ack.acknowledge();
            log.info("Kafka 메시지 처리 성공 및 오프셋 커밋 완료 - orderId={}", event.orderId());
        } catch (Exception e) {
            log.error("Kafka 주문 완료 이벤트 처리 실패 - orderId={}", event.orderId(), e);
        }
    }

}
