package kr.hhplus.ecommerce.interfaces.coupon;

import kr.hhplus.ecommerce.application.coupon.dto.CouponResult;
import kr.hhplus.ecommerce.domain.coupon.entity.CouponStatus;

import java.time.LocalDateTime;

public record CouponResponse() {

    public record CreateUserCoupon(
            Long id,
            Long userId,
            Long couponId,
            CouponStatus status,
            LocalDateTime expiredAt
    ) {
        public static CreateUserCoupon from(CouponResult.Issued result) {
            return new CreateUserCoupon(
                    result.id(),
                    result.userId(),
                    result.couponId(),
                    result.status(),
                    result.expiredAt()
            );
        }
    }

}
