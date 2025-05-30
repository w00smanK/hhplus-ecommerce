package kr.hhplus.ecommerce.interfaces.coupon;


import kr.hhplus.ecommerce.domain.coupon.CouponApplyRepository;
import kr.hhplus.ecommerce.domain.coupon.CouponEvent;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponRepository;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponConsumer {
    private final IssuedCouponRepository issuedCouponRepository;
    private final CouponApplyRepository couponApplyRepository;

    @KafkaListener(topics = "coupon.v1.issued", groupId = "coupon-issue")
    public void consume(CouponEvent.CouponIssuedEvent event) {
        Long couponId = event.couponId();
        Long userId = event.userId();

        try {
            // 중복 검사
            if (issuedCouponRepository.findByUserIdAndCouponId(userId, couponId).isPresent()) {
                log.info("이미 발급된 유저: {}", userId);
                return;
            }

            // Redis 재고 확인 및 감소 (성공시 true)
            boolean issued = couponApplyRepository.issueCoupon(userId, couponId);
            if (!issued) {
                log.warn("쿠폰 재고 소진됨: {}", couponId);
                return;
            }

            // 발급 성공 → DB 저장
            issuedCouponRepository.save(new IssuedCoupon(userId, couponId));
            log.info("쿠폰 발급 완료: userId={}, couponId={}", userId, couponId);

        } catch (Exception e) {
            log.error("쿠폰 발급 실패 - userId={}, couponId={}", userId, couponId, e);
        }
    }
}
