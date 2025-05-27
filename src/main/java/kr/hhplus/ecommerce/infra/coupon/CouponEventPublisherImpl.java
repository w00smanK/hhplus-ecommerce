package kr.hhplus.ecommerce.infra.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponEvent;
import kr.hhplus.ecommerce.domain.coupon.CouponEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class CouponEventPublisherImpl implements CouponEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void couponUseEvent(CouponEvent.UseCoupon event) {
        applicationEventPublisher.publishEvent(event);
    }
}
