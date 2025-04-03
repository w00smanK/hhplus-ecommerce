package kr.hhplus.ecommerce.interfaces.presentation.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kr.hhplus.ecommerce.interfaces.presentation.response.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    /**
     * 3. 상품 조회 API
     */
    @Operation(summary = "상품 조회", description = "상품의 정보를 반환 한다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description ="product not found")
    @GetMapping("/api/products/{productId}")
    public ResponseEntity<StatusResponse<ProductsResponse>> getProducts(@PathVariable ("productId") Long id) {
        ProductsResponse data = ProductsResponse.of(mockProducts());
        StatusResponse<ProductsResponse> response = StatusResponse.of(200, "OK", data);
        return ResponseEntity.ok(response);
    }
    private List<ProductResponse> mockProducts() {
        return List.of(
            ProductResponse.builder()
                    .id(12L)
                    .name("상품명")
                    .brand("브랜드")
                    .price(10000)
                    .stock(100)
                    .stockQuantities(List.of(
                            new ProductResponse.StockQuantity("L", 2),
                            new ProductResponse.StockQuantity("M", 3)
                    ))
                    .build()
        );
    }
}
