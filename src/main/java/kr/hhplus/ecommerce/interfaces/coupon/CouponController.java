package kr.hhplus.ecommerce.interfaces.coupon;

import kr.hhplus.ecommerce.application.coupon.CouponFacade;
import kr.hhplus.ecommerce.interfaces.common.StatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController implements CouponApi {

    private final CouponFacade couponFacade;

    @Override
    public StatusResponse<CouponResponse.CreateUserCoupon> createUserCoupon(CouponRequest.Issue request) {
        var result = couponFacade.couponFirstIssue(request.toCriteria());
        return StatusResponse.of(200, "쿠폰 발급 성공", CouponResponse.CreateUserCoupon.from(result));
    }


}
