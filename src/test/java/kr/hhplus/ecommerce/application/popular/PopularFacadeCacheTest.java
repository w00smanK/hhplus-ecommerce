package kr.hhplus.ecommerce.application.popular;

import kr.hhplus.ecommerce.config.CacheType;
import kr.hhplus.ecommerce.config.RedisCacheCleaner;
import kr.hhplus.ecommerce.config.RedisCacheTemplate;
import kr.hhplus.ecommerce.domain.popular.RankService;
import kr.hhplus.ecommerce.domain.popular.dto.RankInfo;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PopularFacadeCacheTest {

    @Mock
    private ProductService productService;

    @Mock
    private RankService rankService;

    @Mock
    private RedisCacheTemplate redisCacheTemplate;

    @Mock
    private RedisCacheCleaner redisCacheCleaner;

    @InjectMocks
    private PopularFacade popularFacade;

    private final String cacheKey = "top:5:days:3";

    @BeforeEach
    void setUp() {
        // RankService 모의 설정
        RankInfo.PopularProducts popularProducts = RankInfo.PopularProducts.of(
                List.of(3L, 2L, 1L)
        );
        when(rankService.getPopularSellRank(any())).thenReturn(popularProducts);

        // ProductService 모의 설정
        List<ProductInfo.Product> products = List.of(
                ProductInfo.Product.builder()
                        .productId(1L)
                        .productName("상품명1")
                        .productPrice(1000L)
                        .build(),
                ProductInfo.Product.builder()
                        .productId(2L)
                        .productName("상품명2")
                        .productPrice(2000L)
                        .build(),
                ProductInfo.Product.builder()
                        .productId(3L)
                        .productName("상품명3")
                        .productPrice(3000L)
                        .build()
        );
        when(productService.getProducts(any())).thenReturn(ProductInfo.Products.of(products));
    }

    @AfterEach
    void tearDown() {
        reset(redisCacheTemplate, rankService, productService);
    }

    @DisplayName("인기 상품을 캐싱 조회 한다.")
    @Test
    void getPopularProducts() {
        // given
        when(redisCacheTemplate.get(eq(CacheType.CacheName.POPULAR_PRODUCT), eq(cacheKey), eq(PopularResult.PopularProducts.class)))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(createMockPopularProducts()));

        // when
        popularFacade.getPopularProducts(PopularCriteria.PopularProducts.ofTop5Days3());

        // then
        verify(redisCacheTemplate, times(1)).get(eq(CacheType.CacheName.POPULAR_PRODUCT), eq(cacheKey), eq(PopularResult.PopularProducts.class));
        verify(rankService, times(1)).getPopularSellRank(any());
        verify(productService, times(1)).getProducts(any());
    }

    @DisplayName("인기 상품을 캐싱 한다.")
    @Test
    void updatePopularProductsForCache() {
        // given
        when(redisCacheTemplate.get(eq(CacheType.CacheName.POPULAR_PRODUCT), eq(cacheKey), eq(PopularResult.PopularProducts.class)))
                .thenReturn(Optional.empty());

        // when
        popularFacade.updatePopularProducts(PopularCriteria.PopularProducts.ofTop5Days3());

        // then
        verify(redisCacheTemplate, times(1)).put(eq(CacheType.CacheName.POPULAR_PRODUCT), eq(cacheKey), any(PopularResult.PopularProducts.class));
        verify(rankService, times(1)).getPopularSellRank(any());
        verify(productService, times(1)).getProducts(any());
    }

    @DisplayName("인기 상품을 캐시 갱신 한다.")
    @Test
    void updatePopularProductsForRefresh() {
        // given
        when(redisCacheTemplate.get(eq(CacheType.CacheName.POPULAR_PRODUCT), eq(cacheKey), eq(String.class)))
                .thenReturn(Optional.of("test"));

        // when
        popularFacade.updatePopularProducts(PopularCriteria.PopularProducts.ofTop5Days3());

        // then
        verify(redisCacheTemplate, times(1)).put(eq(CacheType.CacheName.POPULAR_PRODUCT), eq(cacheKey), any(PopularResult.PopularProducts.class));
        verify(rankService, times(1)).getPopularSellRank(any());
        verify(productService, times(1)).getProducts(any());
    }

    private PopularResult.PopularProducts createMockPopularProducts() {
        List<PopularResult.PopularProduct> products = List.of(
                PopularResult.PopularProduct.of(3L, "상품명3", 3000L),
                PopularResult.PopularProduct.of(2L, "상품명2", 2000L),
                PopularResult.PopularProduct.of(1L, "상품명1", 1000L)
        );
        return PopularResult.PopularProducts.of(products);
    }
}
