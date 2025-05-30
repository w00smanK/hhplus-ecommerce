package kr.hhplus.ecommerce.interfaces.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponService;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.ecommerce.interfaces.common.StatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController implements CouponApi {

    private final CouponService couponService;

    @Override
    public StatusResponse<CouponResponse.CreateUserCoupon> createUserCoupon(CouponRequest.Issue request) {
        IssuedCoupon issuedCoupon = couponService.issueWithRedis(request.toCommand());
        CouponResponse.CreateUserCoupon response = CouponResponse.CreateUserCoupon.from(issuedCoupon);
        return StatusResponse.of(200, "쿠폰 발급 성공", response);
    }

    @Override
    public StatusResponse<String> createUserCouponWithKafka(CouponRequest.Issue request) {
        couponService.issueCouponKafka(request.toCommand());
        return StatusResponse.of(200, "쿠폰 발급 요청 성공", "쿠폰 발급이 요청되었습니다. 잠시 후 발급됩니다.");
    }
}
