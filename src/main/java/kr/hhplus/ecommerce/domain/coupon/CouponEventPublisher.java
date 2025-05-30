package kr.hhplus.ecommerce.domain.coupon;

public interface CouponEventPublisher {

    void couponUseEvent(CouponEvent.UseCoupon event);

    void publish(CouponEvent.IssueCoupon event);

}
