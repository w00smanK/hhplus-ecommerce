package kr.hhplus.ecommerce.interfaces.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.ecommerce.interfaces.common.StatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Product", description = "상품 관련 API")
public interface ProductApi {


    @Operation(summary = "상품 전체 목록 조회", description = "전체 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponse.ProductList.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                    content = @Content(schema = @Schema(
                            example = "{\"code\":500,\"message\":\"ERROR\"}"
                    )))
    })
    @GetMapping
    StatusResponse<ProductResponse.ProductList> findAll();

    @Operation(summary = "상품 단건 조회", description = "상품 ID로 상품 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponse.ProductDetail.class))),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(
                            example = "{\"code\":404,\"message\":\"ERROR\"}"
                    ))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                    content = @Content(schema = @Schema(
                            example = "{\"code\":500,\"message\":\"ERROR\"}"
                    )))
    })
    @GetMapping("/{productId}")
    StatusResponse<ProductResponse.ProductDetail> findProduct(
            @Parameter(description = "상품 ID", example = "1") @PathVariable("productId") Long productId
    );

    @Operation(summary = "인기 상품 목록 조회", description = "날짜별 인기 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponse.ProductList.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터",
                    content = @Content(schema = @Schema(
                            example = "{\"code\":400,\"message\":\"ERROR\"}"
                    ))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                    content = @Content(schema = @Schema(
                            example = "{\"code\":500,\"message\":\"ERROR\"}"
                    )))
    })
    @GetMapping("/best")
    StatusResponse<ProductResponse.ProductList> findBestSelling(
            @Parameter(description = "조회 날짜 (ISO 형식: yyyy-MM-dd)", example = "2023-05-01") 
            @RequestParam(value = "date", required = true) String date,

            @Parameter(description = "조회할 상품 개수", example = "10") 
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    );
}
