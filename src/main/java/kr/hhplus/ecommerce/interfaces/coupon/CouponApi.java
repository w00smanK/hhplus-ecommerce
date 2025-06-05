package kr.hhplus.ecommerce.interfaces.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.ecommerce.interfaces.common.StatusResponse;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Operation(summary = "Kafka 기반 선착순 쿠폰 발급", description = "Kafka를 통해 선착순 쿠폰 발급을 요청합니다.")
    @ApiResponse(responseCode = "200", description = "쿠폰 발급 요청 성공")
    @PostMapping("/kafka")
    StatusResponse<String> createUserCouponWithKafka(
            @RequestBody CouponRequest.Issue request
    );

    @Operation(summary = "쿠폰 생성", description = "쿠폰 생성")
    @ApiResponse(responseCode = "200", description = "쿠폰 생성 완료")
    @PostMapping("/{couponId}/stock")
    StatusResponse<String> setCouponStock(
            @PathVariable Long couponId,
            @RequestBody CouponRequest.CouponSetting request
    );
}
