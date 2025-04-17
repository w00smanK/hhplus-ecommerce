package kr.hhplus.ecommerce.interfaces.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.ecommerce.interfaces.presentation.response.StatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Product", description = "상품 관련 API")
public interface ProductApi {

    @Operation(summary = "상품 전체 목록 조회", description = "전체 상품 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ProductResponse.ProductList.class)))
    @GetMapping
    StatusResponse<ProductResponse.ProductList> findAll();

    @Operation(summary = "상품 단건 조회", description = "상품 ID로 상품 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ProductResponse.ProductDetail.class)))
    @GetMapping("/{productId}")
    StatusResponse<ProductResponse.ProductDetail> findProduct(
            @Parameter(description = "상품 ID", example = "1") @PathVariable("productId") Long productId
    );
}
