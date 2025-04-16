package kr.hhplus.ecommerce.application.coupon.dto;


import kr.hhplus.ecommerce.domain.coupon.dto.CouponCommand;

public record CouponCriteria() {


    public record Issue(
            Long userId,
            Long couponId
    ) {
        public CouponCommand.Issue toCommand() {
            return new CouponCommand.Issue(userId, couponId);
        }
    }
}
