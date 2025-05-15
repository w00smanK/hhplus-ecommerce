package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;

public interface CouponRedisRepository {

    /**
     * 쿠폰 초기화 - 쿠폰 ID와 수량을 Redis에 저장
     */
    void initializeCoupon(Coupon coupon);

    /**
     * 쿠폰 발급 - Redis의 Sorted Set에서 멤버 하나를 제거하고 발급 처리
     * 쿠폰 발급 요청 시각을 스코어(score)로 저장하여, 선착순 순서 보장 및 중복 발급 방지
     */
    boolean issueCoupon(Long userId, Long couponId);

    long getCouponStock(Long couponId);

    boolean hasIssuedCoupon(Long userId, Long couponId);

    void rollbackIssuance(Long userId, Long couponId);
}
