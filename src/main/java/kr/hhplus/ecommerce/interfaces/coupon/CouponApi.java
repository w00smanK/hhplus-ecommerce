package kr.hhplus.ecommerce.interfaces.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.ecommerce.interfaces.common.StatusResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Coupon", description = "쿠폰 관련 API")
public interface CouponApi {

    @Operation(summary = "선착순 쿠폰 발급", description = "사용자에게 선착순 쿠폰을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공")
    @PostMapping
    StatusResponse<CouponResponse.CreateUserCoupon> createUserCoupon(
            @RequestBody CouponRequest.Issue request
    );
}
