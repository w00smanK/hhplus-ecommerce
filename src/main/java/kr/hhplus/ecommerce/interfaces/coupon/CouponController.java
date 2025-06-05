package kr.hhplus.ecommerce.interfaces.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponService;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.ecommerce.infra.coupon.CouponApplyRepositoryImpl;
import kr.hhplus.ecommerce.interfaces.common.StatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController implements CouponApi {

    private final CouponService couponService;
    private final CouponApplyRepositoryImpl couponApplyRepository;
    @Override
    public StatusResponse<CouponResponse.CreateUserCoupon> createUserCoupon(CouponRequest.Issue request) {
        IssuedCoupon issuedCoupon = couponService.issueWithRedis(request.toCommand());
        CouponResponse.CreateUserCoupon response = CouponResponse.CreateUserCoupon.from(issuedCoupon);
        return StatusResponse.of(200, "쿠폰 발급 성공", response);
    }

    @Override
    public StatusResponse<String> createUserCouponWithKafka(CouponRequest.Issue request) {
        couponService.requestCouponIssue(request.toCommand());
        return StatusResponse.of(200, "쿠폰 발급 요청 성공", "쿠폰 발급이 요청되었습니다. 잠시 후 발급됩니다.");
    }

    @Override
    public StatusResponse<String> setCouponStock(Long couponId, CouponRequest.CouponSetting request) {
        couponApplyRepository.setManualCouponStock(couponId, request.quantity());
        String message = String.format("쿠폰 ID %d에 %d개 재고 설정 완료", couponId, request.quantity());
        return StatusResponse.success(message);
    }
}
