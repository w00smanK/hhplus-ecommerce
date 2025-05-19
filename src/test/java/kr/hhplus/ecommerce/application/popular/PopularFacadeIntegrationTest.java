package kr.hhplus.ecommerce.application.popular;

import kr.hhplus.ecommerce.application.popular.dto.PopularCriteria;
import kr.hhplus.ecommerce.application.popular.dto.PopularResult;
import kr.hhplus.ecommerce.config.CacheType;
import kr.hhplus.ecommerce.config.RedisCacheCleaner;
import kr.hhplus.ecommerce.config.RedisCacheTemplate;
import kr.hhplus.ecommerce.domain.product.ProductRepository;
import kr.hhplus.ecommerce.domain.product.entity.Product;
import kr.hhplus.ecommerce.domain.product.entity.ProductSellingStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PopularFacadeIntegrationTest {

    @Autowired
    private PopularFacade popularFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private RedisCacheCleaner redisCacheCleaner;
    
    private final String cacheKey = "top:5:days:3";

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        product1 = createProduct("상품명1", 1000L, ProductSellingStatus.SELLING);
        product2 = createProduct("상품명2", 2000L, ProductSellingStatus.SELLING);
        product3 = createProduct("상품명3", 3000L, ProductSellingStatus.STOP_SELLING);
        
        // 테스트 데이터 저장
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        
        // 인기 상품 랭킹 생성 (전일 데이터)
        popularFacade.createDailyRankAt(LocalDate.now().minusDays(1));
    }

    @AfterEach
    void tearDown() {
        // 캐시 초기화
        redisCacheCleaner.clean();
    }

    @DisplayName("인기 상품을 캐싱 조회 한다.")
    @Test
    void getPopularProducts() {
        // given
        Optional<PopularResult.PopularProducts> emptyCached = redisCacheTemplate.get(
                CacheType.CacheName.POPULAR_PRODUCT, cacheKey, PopularResult.PopularProducts.class);

        // when
        PopularResult.PopularProducts result = popularFacade.getPopularProducts(
                PopularCriteria.PopularProducts.ofTop5Days3());

        // then
        assertThat(emptyCached).isEmpty();
        
        // 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getProducts()).isNotEmpty();
        
        // 캐시 검증
        Optional<PopularResult.PopularProducts> cached = redisCacheTemplate.get(
                CacheType.CacheName.POPULAR_PRODUCT, cacheKey, PopularResult.PopularProducts.class);
        assertThat(cached).isPresent();
    }

    @DisplayName("인기 상품을 캐싱 한다.")
    @Test
    void updatePopularProductsForCache() {
        // given
        Optional<PopularResult.PopularProducts> emptyCached = redisCacheTemplate.get(
                CacheType.CacheName.POPULAR_PRODUCT, cacheKey, PopularResult.PopularProducts.class);

        // when
        PopularResult.PopularProducts result = popularFacade.updatePopularProducts(
                PopularCriteria.PopularProducts.ofTop5Days3());

        // then
        assertThat(emptyCached).isEmpty();
        
        // 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getProducts()).isNotEmpty();
        
        // 캐시 검증
        Optional<PopularResult.PopularProducts> cached = redisCacheTemplate.get(
                CacheType.CacheName.POPULAR_PRODUCT, cacheKey, PopularResult.PopularProducts.class);
        assertThat(cached).isPresent();
    }

    @DisplayName("인기 상품을 캐시 갱신 한다.")
    @Test
    void updatePopularProductsForRefresh() {
        // given
        redisCacheTemplate.put(CacheType.CacheName.POPULAR_PRODUCT, cacheKey, "test");
        Optional<String> existCached = redisCacheTemplate.get(
                CacheType.CacheName.POPULAR_PRODUCT, cacheKey, String.class);

        // when
        PopularResult.PopularProducts result = popularFacade.updatePopularProducts(
                PopularCriteria.PopularProducts.ofTop5Days3());

        // then
        assertThat(existCached).isPresent();
        assertThat(existCached.get()).isEqualTo("test");
        
        // 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getProducts()).isNotEmpty();
        
        // 캐시 검증
        Optional<PopularResult.PopularProducts> cached = redisCacheTemplate.get(
                CacheType.CacheName.POPULAR_PRODUCT, cacheKey, PopularResult.PopularProducts.class);
        assertThat(cached).isPresent();
    }
    
    private Product createProduct(String name, Long price, ProductSellingStatus status) {
        return Product.create(name, price, status);
    }
}