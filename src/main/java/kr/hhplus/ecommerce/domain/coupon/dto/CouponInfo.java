package kr.hhplus.ecommerce.domain.coupon.dto;

import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import kr.hhplus.ecommerce.domain.coupon.entity.CouponStatus;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import lombok.Builder;

import java.time.LocalDateTime;

public record CouponInfo() {

    @Builder
    public record CouponStock(
            Long couponId,
            Long discountPrice,
            CouponStatus status,
            LocalDateTime usedAt,
            LocalDateTime expiredAt

    ) {
        public static CouponStock from() {
            return new CouponStock(null, null, null, null, null);
        }
        public static CouponStock from(Coupon coupon, IssuedCoupon issuedCoupon) {
            return CouponStock.builder()
                    .couponId(coupon.getId())
                    .discountPrice(coupon.getDiscountPrice())
                    .status(issuedCoupon.getStatus())
                    .usedAt(issuedCoupon.getUsedAt())
                    .expiredAt(issuedCoupon.getExpiredAt())
                    .build();
        }
    }
}
