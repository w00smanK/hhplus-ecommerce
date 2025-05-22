package kr.hhplus.ecommerce.interfaces.coupon;

import kr.hhplus.ecommerce.domain.coupon.dto.CouponCommand;

public record CouponRequest() {

    public record Issue(
            long userId,
            long couponId
    ) {
        public CouponCommand.Issue toCommand() {
            return new CouponCommand.Issue(userId, couponId);
        }
    }

}
