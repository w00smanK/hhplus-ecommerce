package kr.hhplus.ecommerce.domain.coupon.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IssuedCouponCommand {

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class Publish {

        private final Long userId;
        private final Long couponId;
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class UsableCoupon {

        private final Long userId;
        private final Long couponId;

    }
}
