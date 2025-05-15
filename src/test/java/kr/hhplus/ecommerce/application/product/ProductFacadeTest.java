package kr.hhplus.ecommerce.application.product;

import kr.hhplus.ecommerce.application.product.dto.ProductCriteria;
import kr.hhplus.ecommerce.application.product.dto.ProductResult;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductFacadeTest")
class ProductFacadeTest {

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private ProductFacade productFacade;

    @Test
    @DisplayName("인기 상품 조회 테스트")
    void findBestSellingTest() {
        // given
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        int limit = 5;
        ProductCriteria.Best criteria = new ProductCriteria.Best(date, limit);

        // Mock OrderService.findBestSelling
        List<OrderInfo.Best> bestSellingProducts = List.of(
            new OrderInfo.Best(101L, 150L),
            new OrderInfo.Best(102L, 120L),
            new OrderInfo.Best(103L, 100L)
        );
        when(orderService.findBestSelling(any(OrderCommand.FindBest.class))).thenReturn(bestSellingProducts);

        // Mock ProductService.findProductByOptionId
        ProductInfo.ProductDetail product1 = ProductInfo.ProductDetail.builder()
            .productId(1L)
            .brand("Apple")
            .name("iPhone 15")
            .stocks(List.of())
            .build();
        ProductInfo.ProductDetail product2 = ProductInfo.ProductDetail.builder()
            .productId(2L)
            .brand("Samsung")
            .name("Galaxy S24")
            .stocks(List.of())
            .build();
        ProductInfo.ProductDetail product3 = ProductInfo.ProductDetail.builder()
            .productId(3L)
            .brand("Google")
            .name("Pixel 8")
            .stocks(List.of())
            .build();

        when(productService.findProductByOptionId(any(ProductCommand.FindByProductOptionId.class)))
            .thenReturn(product1, product2, product3);

        // when
        ProductResult.ProductList result = productFacade.findBestSelling(criteria);

        // then
        assertThat(result).isNotNull();
        assertThat(result.products()).hasSize(3);
        assertThat(result.products().get(0).productId()).isEqualTo(1L);
        assertThat(result.products().get(0).brand()).isEqualTo("Apple");
        assertThat(result.products().get(0).name()).isEqualTo("iPhone 15");
        assertThat(result.products().get(1).productId()).isEqualTo(2L);
        assertThat(result.products().get(1).brand()).isEqualTo("Samsung");
        assertThat(result.products().get(1).name()).isEqualTo("Galaxy S24");
        assertThat(result.products().get(2).productId()).isEqualTo(3L);
        assertThat(result.products().get(2).brand()).isEqualTo("Google");
        assertThat(result.products().get(2).name()).isEqualTo("Pixel 8");
    }

    @Test
    @DisplayName("인기 상품 캐시 갱신 테스트")
    void refreshBestProductCacheTest() {
        // given
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        int limit = 5;
        ProductCriteria.Best criteria = new ProductCriteria.Best(date, limit);

        // Mock OrderService.findBestSelling
        List<OrderInfo.Best> bestSellingProducts = List.of(
            new OrderInfo.Best(101L, 150L),
            new OrderInfo.Best(102L, 120L)
        );
        when(orderService.findBestSelling(any(OrderCommand.FindBest.class))).thenReturn(bestSellingProducts);

        // Mock ProductService.findProductByOptionId
        ProductInfo.ProductDetail product1 = ProductInfo.ProductDetail.builder()
            .productId(1L)
            .brand("Apple")
            .name("iPhone 15")
            .stocks(List.of())
            .build();
        ProductInfo.ProductDetail product2 = ProductInfo.ProductDetail.builder()
            .productId(2L)
            .brand("Samsung")
            .name("Galaxy S24")
            .stocks(List.of())
            .build();

        when(productService.findProductByOptionId(any(ProductCommand.FindByProductOptionId.class)))
            .thenReturn(product1, product2);

        // when
        ProductResult.ProductList result = productFacade.refreshBestProductCache(criteria);

        // then
        assertThat(result).isNotNull();
        assertThat(result.products()).hasSize(2);
        assertThat(result.products().get(0).productId()).isEqualTo(1L);
        assertThat(result.products().get(0).brand()).isEqualTo("Apple");
        assertThat(result.products().get(0).name()).isEqualTo("iPhone 15");
        assertThat(result.products().get(1).productId()).isEqualTo(2L);
        assertThat(result.products().get(1).brand()).isEqualTo("Samsung");
        assertThat(result.products().get(1).name()).isEqualTo("Galaxy S24");
    }
}