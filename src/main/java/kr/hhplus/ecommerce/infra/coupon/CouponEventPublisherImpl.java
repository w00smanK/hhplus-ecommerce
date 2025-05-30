package kr.hhplus.ecommerce.infra.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponEvent;
import kr.hhplus.ecommerce.domain.coupon.CouponEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class CouponEventPublisherImpl implements CouponEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void couponUseEvent(CouponEvent.UseCoupon event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishEvent(CouponEvent.CouponIssuedEvent event) {
        try {
            kafkaTemplate.send("coupon.v1.issue", String.valueOf(event.userId()), event);
            log.info("쿠폰 발급 요청 Kafka 전송 완료 - couponId: {}, userId: {}", event.couponId(), event.userId());
        } catch (Exception e) {
            log.error("쿠폰 발급 요청 Kafka 전송 실패 - couponId: {}, userId: {}", event.couponId(), event.userId(), e);
            throw e;
        }
    }
}
