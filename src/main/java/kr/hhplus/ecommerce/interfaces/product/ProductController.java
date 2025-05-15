package kr.hhplus.ecommerce.interfaces.product;

import kr.hhplus.ecommerce.application.product.ProductFacade;
import kr.hhplus.ecommerce.application.product.dto.ProductCriteria;
import kr.hhplus.ecommerce.interfaces.presentation.response.StatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController implements ProductApi {

    private final ProductFacade productFacade;

    @Override
    public StatusResponse<ProductResponse.ProductList> findAll() {
        return StatusResponse.of(200, "전체 상품 목록 조회 성공", ProductResponse.ProductList.from(productFacade.findAll()));
    }

    @Override
    public StatusResponse<ProductResponse.ProductDetail> findProduct(Long productId) {
        var result = productFacade.findProduct((new ProductCriteria.Find(productId)));
        return StatusResponse.of(200, "단일 상품 조회 성공", ProductResponse.ProductDetail.from(result));
    }

    @Override
    public StatusResponse<ProductResponse.ProductList> findBestSelling(String date, Integer limit) {
        var result = productFacade.findBestSelling(new ProductCriteria.Best(date, limit));
        return StatusResponse.of(200, "인기 상품 목록 조회 성공", ProductResponse.ProductList.from(result));
    }
}
