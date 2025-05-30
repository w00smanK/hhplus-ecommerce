package kr.hhplus.ecommerce.infra.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponKafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(CouponEvent.IssueCoupon event) {
        try {
            kafkaTemplate.send("coupon.v1.issue", String.valueOf(event.issuedCouponId()), event);
            log.info("쿠폰 발급 Kafka 전송 완료 - issuedCouponId: {}", event.issuedCouponId());
        } catch (Exception e) {
            log.error("쿠폰 Kafka 발급 전송 실패", e);
        }
    }
}