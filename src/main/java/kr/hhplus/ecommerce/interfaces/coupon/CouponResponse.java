package kr.hhplus.ecommerce.interfaces.coupon;

import kr.hhplus.ecommerce.domain.coupon.entity.CouponStatus;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;

import java.time.LocalDateTime;

public record CouponResponse() {

    public record CreateUserCoupon(
            Long id,
            Long userId,
            Long couponId,
            CouponStatus status,
            LocalDateTime expiredAt
    ) {
        public static CreateUserCoupon from(IssuedCoupon issuedCoupon) {
            return new CreateUserCoupon(
                    issuedCoupon.getId(),
                    issuedCoupon.getUserId(),
                    issuedCoupon.getCouponId(),
                    issuedCoupon.getStatus(),
                    issuedCoupon.getExpiredAt()
            );
        }
    }

}
