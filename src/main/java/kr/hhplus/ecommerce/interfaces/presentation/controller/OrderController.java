package kr.hhplus.ecommerce.interfaces.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import kr.hhplus.ecommerce.interfaces.presentation.request.OrderRequest;
import kr.hhplus.ecommerce.interfaces.presentation.response.StatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class    OrderController {
    /**
     * 7. 주문 결제 API
     */
    @Operation(summary = "주문 결제 API", description = "사용자가 선택한 상품을 주문하고, 보유 잔액에서 결제 금액을 차감하여 결제를 완료합니다.")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description ="product not found")
    @PostMapping("/api/orders")
    public ResponseEntity<StatusResponse<OrderRequest>> createOrder(@Valid @RequestBody OrderRequest req) {
        return ResponseEntity.ok(StatusResponse.of(200, "OK",req ));
    }
}
