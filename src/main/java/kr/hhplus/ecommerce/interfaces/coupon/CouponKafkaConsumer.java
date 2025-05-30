package kr.hhplus.ecommerce.interfaces.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponEvent;
import kr.hhplus.ecommerce.domain.coupon.CouponService;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponKafkaConsumer {

    private final CouponService couponService;

    @KafkaListener(topics = "coupon.v1.issue", groupId = "coupon-issue")
    public void handleCouponIssue(@Payload CouponEvent.CouponIssuedEvent event) {
        try {
            log.info("쿠폰 발급 요청 수신 - couponId: {}, userId: {}", event.couponId(), event.userId());
            
            // 실제 쿠폰 발급 처리
            couponService.issue(new CouponCommand.Issue(event.userId(), event.couponId()));
            
            log.info("쿠폰 발급 완료 - couponId: {}, userId: {}", event.couponId(), event.userId());
        } catch (Exception e) {
            log.error("쿠폰 발급 실패 - couponId: {}, userId: {}", event.couponId(), event.userId(), e);
            // 실패 시 별도 처리 로직 (DLQ, 재시도 등) 추가 가능
        }
    }
}
