package kr.hhplus.ecommerce.interfaces.coupon;

import kr.hhplus.ecommerce.application.coupon.dto.CouponCriteria;

public record CouponRequest() {

    public record Issue(
            long userId,
            long couponId
    ) {
        public CouponCriteria.Issue toCriteria() {
            return new CouponCriteria.Issue(userId, couponId);
        }
    }

}
