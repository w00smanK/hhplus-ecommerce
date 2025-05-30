package kr.hhplus.ecommerce.domain.coupon;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponEvent {
    public record UseCoupon(
            Long userId,
            Long orderId,
            Long couponId,
            Long issuedCouponId,
            Long discountPrice
    ) {}

    public record IssueCoupon(
            Long userId,
            Long issuedCouponId
    ){}
}
