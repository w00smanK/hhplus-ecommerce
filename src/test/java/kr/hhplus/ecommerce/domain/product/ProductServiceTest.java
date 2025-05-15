package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import kr.hhplus.ecommerce.domain.product.entity.Product;
import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("상품")
class ProductServiceTest {

    @InjectMocks
    ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductStockRepository productStockRepository;
    private Long PRODUCT_ID1;
    private Product PRODUCT1;
    private ProductStock PRODUCT_OPTION1;
    private ProductStock PRODUCT_OPTION2;

    private Long PRODUCT_ID2;
    private Product PRODUCT2;
    private ProductStock PRODUCT_OPTION3;

    @BeforeEach
    void setUp() {
        PRODUCT_ID1 = 1L;
        PRODUCT1 = new Product(PRODUCT_ID1, "NIKE", "에어포스 1");
        PRODUCT_OPTION1 = new ProductStock(101L, "화이트/270", 129000L, 50L);
        PRODUCT_OPTION2 = new ProductStock(102L, "블랙/275", 129000L, 45L);

        PRODUCT_ID2 = 2L;
        PRODUCT2 = new Product(PRODUCT_ID2, "NIKE", "에어맥스 97");
        PRODUCT_OPTION3 = new ProductStock(201L, "실버/270", 189000L, 30L);
    }

    @Test
    @DisplayName("상품 목록 조회")
    void findAll() {
        when(productRepository.findAll()).thenReturn(List.of(PRODUCT1, PRODUCT2));
        when(productStockRepository.findByProductId(PRODUCT_ID1)).thenReturn(List.of(PRODUCT_OPTION1, PRODUCT_OPTION2));
        when(productStockRepository.findByProductId(PRODUCT_ID2)).thenReturn(List.of(PRODUCT_OPTION3));

        ProductInfo.ProductList actualInfo = productService.findAll();

        verify(productRepository).findAll();
        verify(productStockRepository).findByProductId(PRODUCT_ID1);
        verify(productStockRepository).findByProductId(PRODUCT_ID2);

        assertThat(actualInfo.getProducts().get(0).getProductId()).isEqualTo(PRODUCT_ID1);
        assertThat(actualInfo.getProducts().get(0).getBrand()).isEqualTo("NIKE");
        assertThat(actualInfo.getProducts().get(0).getName()).isEqualTo("에어포스 1");
        assertThat(actualInfo.getProducts().get(0).getStocks()).hasSize(2);

        assertThat(actualInfo.getProducts().get(1).getProductId()).isEqualTo(PRODUCT_ID2);
        assertThat(actualInfo.getProducts().get(1).getBrand()).isEqualTo("NIKE");
        assertThat(actualInfo.getProducts().get(1).getName()).isEqualTo("에어맥스 97");
        assertThat(actualInfo.getProducts().get(1).getStocks()).hasSize(1);
    }

    @Nested
    @DisplayName("상품 단건 조회")
    class FindProduct {

        @Test
        @DisplayName("상품 정보 조회 성공")
        void success() {
            when(productRepository.findById(PRODUCT_ID1)).thenReturn(Optional.of(PRODUCT1));
            when(productStockRepository.findByProductId(PRODUCT_ID1)).thenReturn(List.of(PRODUCT_OPTION1, PRODUCT_OPTION2));

            ProductInfo.ProductDetail actualInfo = productService.findProduct(new ProductCommand.Find(PRODUCT_ID1));

            verify(productRepository).findById(PRODUCT_ID1);
            verify(productStockRepository).findByProductId(PRODUCT_ID1);

            assertThat(actualInfo.getProductId()).isEqualTo(PRODUCT_ID1);
            assertThat(actualInfo.getBrand()).isEqualTo("NIKE");
            assertThat(actualInfo.getName()).isEqualTo("에어포스 1");
            assertThat(actualInfo.getStocks()).hasSize(2);
        }

        @Test
        @DisplayName("상품 정보 조회 실패 - 존재하지 않음")
        void notFound() {
            when(productRepository.findById(PRODUCT_ID1)).thenReturn(Optional.empty());

            Exception exception = assertThrows(Exception.class,
                    () -> productService.findProduct(new ProductCommand.Find(PRODUCT_ID1)));

            verify(productRepository).findById(PRODUCT_ID1);
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("재고 차감")
    class ReduceStock {

        @Test
        @DisplayName("재고 충분 - 차감 성공")
        void enoughStock() {
            List<OrderCommand.OrderItem> items = List.of(
                    new OrderCommand.OrderItem(101L, 129000L, 5L),
                    new OrderCommand.OrderItem(102L, 129000L, 3L)
            );

            when(productStockRepository.findByIdWithPessimisticLock(101L)).thenReturn(Optional.of(PRODUCT_OPTION1));
            when(productStockRepository.findByIdWithPessimisticLock(102L)).thenReturn(Optional.of(PRODUCT_OPTION2));

            ProductInfo.StockCheckResult result = productService.reduceStock(OrderCommand.OrderItemList.toCommand(items));

            verify(productStockRepository).findByIdWithPessimisticLock(101L);
            verify(productStockRepository).findByIdWithPessimisticLock(102L);

            assertThat(result.checkStocks()).hasSize(2);
            assertThat(result.checkStocks().get(0).isEnough()).isTrue();
            assertThat(result.checkStocks().get(1).isEnough()).isTrue();
        }

        @Test
        @DisplayName("재고 부족 - 일부 실패")
        void notEnoughStock() {
            List<OrderCommand.OrderItem> items = List.of(
                    new OrderCommand.OrderItem(101L, 129000L, 49L),
                    new OrderCommand.OrderItem(102L, 129000L, 46L) // 재고는 45
            );

            when(productStockRepository.findByIdWithPessimisticLock(101L)).thenReturn(Optional.of(PRODUCT_OPTION1));
            when(productStockRepository.findByIdWithPessimisticLock(102L)).thenReturn(Optional.of(PRODUCT_OPTION2));

            ProductInfo.StockCheckResult result = productService.reduceStock(OrderCommand.OrderItemList.toCommand(items));

            verify(productStockRepository).findByIdWithPessimisticLock(101L);
            verify(productStockRepository).findByIdWithPessimisticLock(102L);

            assertThat(result.checkStocks()).hasSize(2);
            assertThat(result.checkStocks().get(0).isEnough()).isTrue();
            assertThat(result.checkStocks().get(1).isEnough()).isFalse();
        }
    }
}
