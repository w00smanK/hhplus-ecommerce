package kr.hhplus.ecommerce.domain.coupon.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponInfo {

    @Getter
    public static class Coupon {

        private final Long couponId;
        private final double discountRate;

        @Builder
        private Coupon(Long couponId, String name, double discountRate) {
            this.couponId = couponId;
            this.discountRate = discountRate;
        }
    }
}
