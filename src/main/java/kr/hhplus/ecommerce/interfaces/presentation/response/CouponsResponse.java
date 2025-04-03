package kr.hhplus.ecommerce.interfaces.presentation.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CouponsResponse {

    private List<CouponResponse> coupons;
    private CouponsResponse(List<CouponResponse> coupons) {
        this.coupons = coupons;
    }

    public static CouponsResponse of(List<CouponResponse> coupons) {
        return new CouponsResponse(coupons);
    }

}
