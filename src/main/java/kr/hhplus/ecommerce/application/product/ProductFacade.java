package kr.hhplus.ecommerce.application.product;


import kr.hhplus.ecommerce.application.product.dto.ProductCriteria;
import kr.hhplus.ecommerce.application.product.dto.ProductResult;
import kr.hhplus.ecommerce.config.CacheType;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductFacade {


    private final ProductService productService;
    private final OrderService orderService;

    public ProductResult.ProductList findAll() {
        return ProductResult.ProductList.from(productService.findAll());
    }

    public ProductResult.ProductDetail findProduct(ProductCriteria.Find criteria) {
        return ProductResult.ProductDetail.from(productService.findProduct(criteria.toCommand(criteria.productId())));
    }

    /**
     * 인기 판매 상품 조회
     */
    @Cacheable(value = CacheType.CacheName.BEST_PRODUCT, key = "'date:' + #criteria.date() + ':limit:' + #criteria.limit()")
    @Transactional
    public ProductResult.ProductList findBestSelling(ProductCriteria.Best criteria) {
        // 날짜 파싱
        LocalDate date = LocalDate.parse(criteria.date(), DateTimeFormatter.ISO_DATE);

        // 현재 날짜와의 차이 계산 (일 수)
        long daysDiff = LocalDate.now().toEpochDay() - date.toEpochDay();

        // 인기 상품 조회
        List<OrderInfo.Best> bestSellingProducts = orderService.findBestSelling(
            new OrderCommand.FindBest((int) daysDiff, criteria.limit())
        );

        // 상품 옵션 ID 목록 생성
        List<ProductCommand.FindByProductOptionId> optionIds = bestSellingProducts.stream()
            .map(info -> new ProductCommand.FindByProductOptionId(info.productOptionId()))
            .toList();

        // 상품 정보 조회 및 반환
        List<ProductInfo.ProductDetail> productDetails = optionIds.stream()
            .map(id -> productService.findProductByOptionId(id))
            .toList();

        return ProductResult.ProductList.from(ProductInfo.ProductList.of(productDetails));
    }

    /**
     * 인기 판매 상품 캐시 갱신
     */
    @CachePut(value = CacheType.CacheName.BEST_PRODUCT, key = "'date:' + #criteria.date() + ':limit:' + #criteria.limit()")
    @Transactional
    public ProductResult.ProductList refreshBestProductCache(ProductCriteria.Best criteria) {
        return findBestSelling(criteria);
    }
}
