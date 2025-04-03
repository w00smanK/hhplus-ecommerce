package kr.hhplus.ecommerce.interfaces.presentation.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CouponIssuedRequest {

    @NotNull(message = "쿠폰 ID는 필수입니다.")
    private Long couponId;

    private CouponIssuedRequest(Long couponId) {
        this.couponId = couponId;
    }

    public static CouponIssuedRequest of(Long couponId) {
        return new CouponIssuedRequest(couponId);
    }
}
