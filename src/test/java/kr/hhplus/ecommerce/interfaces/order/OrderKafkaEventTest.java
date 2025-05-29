package kr.hhplus.ecommerce.interfaces.order;

import kr.hhplus.ecommerce.domain.order.OrderEvent;
import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;
import kr.hhplus.ecommerce.infra.order.OrderKafkaEventPublisher;
import kr.hhplus.ecommerce.support.TestConsumer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@DirtiesContext
@EmbeddedKafka(
    partitions = 1, 
    topics = {"order.v1.completed"},
    brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092", 
        "port=9092"
    }
)
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9092",
    "spring.kafka.consumer.auto-offset-reset=earliest"
})
public class OrderKafkaEventTest {

    @MockitoSpyBean
    OrderKafkaEventPublisher orderKafkaEventPublisher;

    @Autowired
    TestConsumer.OrderCompletedEventConsumer testConsumer;

    @BeforeEach
    void setUp() {
        testConsumer.clear();
    }

    @Test
    @DisplayName("주문 완료 이벤트 호출")
    void publishOrderKafkaEvent() {
        // given
        OrderEvent.OrderComplete event = new OrderEvent.OrderComplete(
                123L,           // orderId
                456L,           // userId  
                null,           // issuedCouponId
                OrderStatus.COMPLETED,
                50000L,         // totalAmount
                5000L,          // discountAmount
                45000L
        );

        // when: 이벤트 발행
        orderKafkaEventPublisher.publish(event);

        // then: Awaitility로 비동기 실행을 대기
        Awaitility.await()
                .atMost(30, java.util.concurrent.TimeUnit.SECONDS)
                .pollInterval(3, java.util.concurrent.TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Assertions.assertFalse(testConsumer.getReceivedMessages().isEmpty());
                    Assertions.assertEquals(event.orderId(), testConsumer.getReceivedMessages().get(0).orderId());
                    Assertions.assertEquals(event.userId(), testConsumer.getReceivedMessages().get(0).userId());
                    Assertions.assertEquals(event.paymentAmount(), testConsumer.getReceivedMessages().get(0).paymentAmount());
                });
    }

}
