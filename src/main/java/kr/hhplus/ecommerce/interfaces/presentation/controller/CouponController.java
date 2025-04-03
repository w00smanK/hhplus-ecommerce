package kr.hhplus.ecommerce.interfaces.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kr.hhplus.ecommerce.interfaces.presentation.request.CouponIssuedRequest;
import kr.hhplus.ecommerce.interfaces.presentation.response.CouponResponse;
import kr.hhplus.ecommerce.interfaces.presentation.response.CouponsResponse;
import kr.hhplus.ecommerce.interfaces.presentation.response.SimpleResponse;
import kr.hhplus.ecommerce.interfaces.presentation.response.StatusResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class CouponController {


    /**
     * 6. 쿠폰 조회 API
     */
    @Operation(summary = "쿠폰 조회 API", description = " 쿠폰 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description ="coupon is ended")
    @ApiResponse(responseCode = "404", description ="coupon is not exist")
    @GetMapping("/{userId}/coupons")
    public ResponseEntity<StatusResponse<CouponsResponse>> getCoupons(@PathVariable("userId") Long id) {
        CouponsResponse data = CouponsResponse.of(mockCoupons());
        StatusResponse<CouponsResponse> response = StatusResponse.of(200, "OK", data);
        return ResponseEntity.ok(response);
    }

    /**
     * 5. 쿠폰 발급 API
     */
    @Operation(summary = "쿠폰 발급 API", description = " 쿠폰 발급")
    @ApiResponse(responseCode = "200", description = "발급 성공")
    @ApiResponse(responseCode = "400", description ="coupon is ended")

    @PostMapping("/{userId}/coupons")
    public ResponseEntity<SimpleResponse> publishCoupon(@PathVariable("userId") Long id,
                                                        @Valid @RequestBody CouponIssuedRequest req) {
        SimpleResponse response = new SimpleResponse(200, "OK");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    private List<CouponResponse> mockCoupons() {
        return List.of(
            CouponResponse.builder()
                .id(1L)
                .name("쿠폰명")
                .discountRate(0.1)
                .expiredAt("2025-04-30")
                .build()
        );
    }
}
