package kr.hhplus.ecommerce.application.rank;

import kr.hhplus.ecommerce.application.rank.dto.RankCriteria;
import kr.hhplus.ecommerce.application.rank.dto.RankResult;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankFacadeIntegrationTest {

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @Mock
    private RankService rankService;

    @InjectMocks
    private RankFacade rankFacade;

    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
    }

    @Test
    @DisplayName("일별 판매 순위 생성 테스트 - 결제 완료 상품이 있는 경우")
    void createDailyRankAtWithPaidProducts() {
        // Given
        OrderInfo.PaidProduct paidProduct1 = new OrderInfo.PaidProduct(1L, 5L);
        OrderInfo.PaidProduct paidProduct2 = new OrderInfo.PaidProduct(2L, 3L);
        List<OrderInfo.PaidProduct> paidProductList = Arrays.asList(paidProduct1, paidProduct2);
        
        OrderInfo.PaidProducts paidProducts = new OrderInfo.PaidProducts(paidProductList);
        
        // When
        when(orderService.getPaidProducts(any(OrderCommand.DateQuery.class))).thenReturn(paidProducts);
        
        // Then
        rankFacade.createDailyRankAt(today);
        
        verify(orderService).getPaidProducts(any(OrderCommand.DateQuery.class));
        verify(rankService).createSellRank(any(RankCommand.CreateList.class));
    }

    @Test
    @DisplayName("인기 상품 랭킹 조회 테스트 - Redis 캐시에서 조회")
    void getRankProductsFromCache() {
        // Given
        int top = 5;
        int days = 1;
        RankCriteria criteria = new RankCriteria(top, days);
        
        List<Long> productIds = Arrays.asList(1L, 2L, 3L);
        RankInfo rankInfo = RankInfo.of(productIds);
        
        ProductInfo.RankProduct rankProduct1 = ProductInfo.RankProduct.builder()
                .productId(1L)
                .productName("Product 1")
                .productPrice(10000L)
                .build();
                
        ProductInfo.RankProduct rankProduct2 = ProductInfo.RankProduct.builder()
                .productId(2L)
                .productName("Product 2")
                .productPrice(20000L)
                .build();
                
        ProductInfo.RankProduct rankProduct3 = ProductInfo.RankProduct.builder()
                .productId(3L)
                .productName("Product 3")
                .productPrice(30000L)
                .build();
        
        List<ProductInfo.RankProduct> rankProducts = Arrays.asList(rankProduct1, rankProduct2, rankProduct3);
        ProductInfo.RankProducts productRanks = ProductInfo.RankProducts.of(rankProducts);
        
        // When
        when(rankService.getRankProducts(any(RankCommand.RankQuery.class))).thenReturn(rankInfo);
        when(productService.rankProducts(any(ProductCommand.Products.class))).thenReturn(productRanks);
        
        // Then
        RankResult result = rankFacade.getRankProducts(criteria);
        
        verify(rankService).getRankProducts(any(RankCommand.RankQuery.class));
        verify(productService).rankProducts(any(ProductCommand.Products.class));
        
        assertNotNull(result);
        assertEquals(3, result.getProducts().size());
        assertEquals(1L, result.getProducts().get(0).productId());
        assertEquals("Product 1", result.getProducts().get(0).productName());
    }
}