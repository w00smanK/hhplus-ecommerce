package kr.hhplus.ecommerce.domain.coupon.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssuedCouponInfo {

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class UsableCoupon {

        private final Long userCouponId;

    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class Coupons {

        private final List<Coupon> coupons;

    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    @Builder
    public static class Coupon {

        private final Long userCouponId;
        private final Long couponId;
        private final LocalDateTime issuedAt;

    }
}
