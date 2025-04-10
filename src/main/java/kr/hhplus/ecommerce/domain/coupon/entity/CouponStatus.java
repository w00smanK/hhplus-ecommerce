package kr.hhplus.ecommerce.domain.coupon.entity;

public enum CouponStatus {

    READY,     // 발급 가능
    PUBLISHED, // 이미 발급됨
    EXPIRED,   // 만료됨
    SOLD_OUT;  // 수량 없음

    public boolean cannotPublishable() {
        return this != READY;
    }
}
