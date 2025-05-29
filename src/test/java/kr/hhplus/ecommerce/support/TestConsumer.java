package kr.hhplus.ecommerce.support;

import kr.hhplus.ecommerce.domain.order.OrderEvent;
import lombok.Getter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestConsumer {

    @Component
    public static class OrderCompletedEventConsumer {
        
        @Getter
        private final List<OrderEvent.OrderComplete> receivedMessages = new ArrayList<>();

        @KafkaListener(topics = "order.v1.completed", groupId = "test-group")
        public void consume(OrderEvent.OrderComplete event) {
            receivedMessages.add(event);
        }

        public void clear() {
            receivedMessages.clear();
        }
    }
}
