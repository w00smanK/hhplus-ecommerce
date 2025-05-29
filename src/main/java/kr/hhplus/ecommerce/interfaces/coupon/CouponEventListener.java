package kr.hhplus.ecommerce.interfaces.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponService;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponCommand;
import kr.hhplus.ecommerce.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponEventListener {

    private final CouponService couponService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void hangleCouponUse(OrderEvent.OrderCreated event) {
        if (event.couponId() == null) {
            return;
        }
        try {
            couponService.use(new CouponCommand.Use(event.userId(), event.couponId(), event.orderId()));
            log.info("쿠폰 사용 완료 - orderId: {}", event.orderId());
        } catch (Exception e) {
            log.error("쿠폰 사용 실패 - orderId: {}, couponId: {}", event.orderId(), event.couponId(), e);
        }
    }

}
