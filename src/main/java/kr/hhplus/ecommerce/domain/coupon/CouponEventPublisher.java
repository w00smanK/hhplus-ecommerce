package kr.hhplus.ecommerce.domain.coupon;

public interface CouponEventPublisher {

    void use(CouponEvent.UseCoupon event);
}
