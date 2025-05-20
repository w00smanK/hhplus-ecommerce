package kr.hhplus.ecommerce.application.rank;

import kr.hhplus.ecommerce.application.rank.dto.RankCriteria;
import kr.hhplus.ecommerce.application.rank.dto.RankResult;
import kr.hhplus.ecommerce.config.CacheType;
import kr.hhplus.ecommerce.config.RedisCacheTemplate;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import kr.hhplus.ecommerce.domain.product.entity.Product;
import kr.hhplus.ecommerce.domain.rank.RankService;
import kr.hhplus.ecommerce.domain.rank.dto.RankCommand;
import kr.hhplus.ecommerce.domain.rank.dto.RankInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RankFacadeCacheTest {

    @Mock
    private ProductService productService;

    @Mock
    private RankService rankService;

    @Mock
    private RedisCacheTemplate redisCacheTemplate;

    @InjectMocks
    private RankFacade rankFacade;

    private final List<Long> productIds = Arrays.asList(1L, 2L, 3L);
    private final List<Product> products = Arrays.asList(
            createProduct(1L, "나이키 에어포스", 129000L),
            createProduct(2L, "나이키 덩크 로우", 139000L),
            createProduct(3L, "나이키 에어맥스", 159000L)
    );
    private final RankInfo rankInfo = RankInfo.of(productIds);
    private final RankCriteria criteria = RankCriteria.of(10, 7);
    private final String cacheKey = "top:10:days:7";

    @BeforeEach
    void setUp() {
        // ProductInfo.RankProducts 객체 생성
        ProductInfo.RankProducts rankProducts = ProductInfo.RankProducts.of(products);
        
        // RankService 모의 설정
        when(rankService.getRankProducts(any(RankCommand.RankQuery.class)))
                .thenReturn(rankInfo);
        
        // ProductService 모의 설정
        when(productService.rankProducts(any(ProductCommand.Products.class)))
                .thenReturn(rankProducts);
    }

    @Test
    @DisplayName("캐시에 데이터가 없을 때 랭킹 상품을 조회하고 캐시에 저장한다")
    void getRankProductsWhenCacheNotExists() {
        // given
        when(redisCacheTemplate.get(eq(CacheType.CacheName.RANK_PRODUCT), eq(cacheKey), eq(RankResult.class)))
                .thenReturn(Optional.empty());

        // when
        RankResult result = rankFacade.getRankProducts(criteria);

        // then
        assertThat(result.getProducts()).hasSize(3);
        assertThat(result.getProducts().get(0).productName()).isEqualTo("나이키 에어포스");
        assertThat(result.getProducts().get(1).productName()).isEqualTo("나이키 덩크 로우");
        assertThat(result.getProducts().get(2).productName()).isEqualTo("나이키 에어맥스");
        
            verify(redisCacheTemplate).get(eq(CacheType.CacheName.RANK_PRODUCT), eq(cacheKey), eq(RankResult.class));
        verify(rankService).getRankProducts(any(RankCommand.RankQuery.class));
        verify(productService).rankProducts(any(ProductCommand.Products.class));
        verify(redisCacheTemplate).put(eq(CacheType.CacheName.RANK_PRODUCT), eq(cacheKey), any(RankResult.class));
    }

    @Test
    @DisplayName("캐시에 데이터가 있을 때 캐시에서 랭킹 상품을 조회한다")
    void getRankProductsWhenCacheExists() {
        // given
        RankResult cachedResult = createRankResult();
        when(redisCacheTemplate.get(eq(CacheType.CacheName.RANK_PRODUCT), eq(cacheKey), eq(RankResult.class)))
                .thenReturn(Optional.of(cachedResult));

        // when
        RankResult result = rankFacade.getRankProducts(criteria);

        // then
        assertThat(result.getProducts()).hasSize(3);
        assertThat(result.getProducts().get(0).productName()).isEqualTo("나이키 에어포스");
        
        verify(redisCacheTemplate).get(eq(CacheType.CacheName.RANK_PRODUCT), eq(cacheKey), eq(RankResult.class));
        verify(rankService, never()).getRankProducts(any(RankCommand.RankQuery.class));
        verify(productService, never()).rankProducts(any(ProductCommand.Products.class));
        verify(redisCacheTemplate, never()).put(any(), any(), any());
    }

    @Test
    @DisplayName("강제 업데이트 시 캐시 내용을 갱신한다")
    void updateRankProducts() {
        // given
        RankResult updatedResult = createRankResult();
        
        // rankFacade.getRankProducts 메서드를 모의하여 항상 updatedResult를 반환하도록 설정
        when(rankService.getRankProducts(any(RankCommand.RankQuery.class)))
                .thenReturn(rankInfo);
        when(productService.rankProducts(any(ProductCommand.Products.class)))
                .thenReturn(ProductInfo.RankProducts.of(products));

        // when
        RankResult result = rankFacade.updateRankProducts(criteria);

        // then
        assertThat(result.getProducts()).hasSize(3);
        
        verify(rankService).getRankProducts(any(RankCommand.RankQuery.class));
        verify(productService).rankProducts(any(ProductCommand.Products.class));
        verify(redisCacheTemplate).put(eq(CacheType.CacheName.RANK_PRODUCT), eq(cacheKey), any(RankResult.class));
    }

    private Product createProduct(Long id, String name, Long price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private RankResult createRankResult() {
        List<RankResult.RankProduct> rankProducts = Arrays.asList(
                new RankResult.RankProduct(1L, "나이키 에어포스", 129000L),
                new RankResult.RankProduct(2L, "나이키 덩크 로우", 139000L),
                new RankResult.RankProduct(3L, "나이키 에어맥스", 159000L)
        );
        return RankResult.of(rankProducts);
    }
}
