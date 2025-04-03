package kr.hhplus.ecommerce.interfaces.presentation.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kr.hhplus.ecommerce.interfaces.presentation.response.ProductResponse;
import kr.hhplus.ecommerce.interfaces.presentation.response.StatProductsResponse;
import kr.hhplus.ecommerce.interfaces.presentation.response.StatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatController {

    /**
     * 4. 상위 TOP5 상품 조회 API
     */
    @Operation(summary = "상위 TOP5 상품 조회", description = " 지정된 기간 동안 가장 많이 판매된 상위 N개의 상품을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description ="invalid parameters")
    @GetMapping("/api/products/top5")
    public ResponseEntity<StatusResponse<StatProductsResponse>> getStats() {
        List<ProductResponse> summaryProducts = mockProducts().stream()
                .map(ProductResponse::toSummary)
                .toList();

        StatProductsResponse data = StatProductsResponse.of(summaryProducts);
        return ResponseEntity.ok(StatusResponse.of(200, "OK", data));
    }

    private List<ProductResponse> mockProducts() {
        return List.of(
                ProductResponse.builder()
                        .id(123L)
                        .name("상품명1")
                        .brand("브랜드1")
                        .price(10000)
                        .stock(10)
                        .stockQuantities(List.of())
                        .build(),
                ProductResponse.builder()
                        .id(456L)
                        .name("상품명2")
                        .brand("브랜드2")
                        .price(20000)
                        .stock(15)
                        .stockQuantities(List.of())
                        .build(),
                ProductResponse.builder()
                        .id(789L)
                        .name("상품명3")
                        .brand("브랜드3")
                        .price(30000)
                        .stock(8)
                        .stockQuantities(List.of())
                        .build(),
                ProductResponse.builder()
                        .id(101L)
                        .name("상품명4")
                        .brand("브랜드4")
                        .price(15000)
                        .stock(20)
                        .stockQuantities(List.of())
                        .build(),
                ProductResponse.builder()
                        .id(102L)
                        .name("상품명5")
                        .brand("브랜드5")
                        .price(12000)
                        .stock(5)
                        .stockQuantities(List.of())
                        .build()
        );
    }

}
