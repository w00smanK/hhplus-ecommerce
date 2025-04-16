package kr.hhplus.ecommerce.application.product;


import kr.hhplus.ecommerce.application.product.dto.ProductCriteria;
import kr.hhplus.ecommerce.application.product.dto.ProductResult;
import kr.hhplus.ecommerce.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductFacade {


    private final ProductService productService;

    @Transactional(readOnly = true)
    public ProductResult.ProductList findAll() {
        return ProductResult.ProductList.from(productService.findAll());
    }

    @Transactional(readOnly = true)
    public ProductResult.ProductDetail findProduct(ProductCriteria.Find criteria) {
        return ProductResult.ProductDetail.from(productService.findProduct(criteria.toCommand()));
    }

}
