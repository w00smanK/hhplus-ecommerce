package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;

public interface CouponApplyRepository {


    void initializeCoupon(Coupon coupon);

    boolean issueCoupon(Long userId, Long couponId);

    boolean issueCouponEvent(Long userId, Long couponId);

    long getCouponStock(Long couponId);

    boolean hasIssuedCoupon(Long userId, Long couponId);

    void rollbackIssuance(Long userId, Long couponId);

    boolean addToIssueQueue(Long userId, Long couponId);
}
