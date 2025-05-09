package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.config.exception.CustomException;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import kr.hhplus.ecommerce.domain.product.entity.Product;
import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("[통합테스트] ProductService")
@Transactional
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    private Product soccerUniform;
    private ProductStock homeJersey;
    private ProductStock awayJersey;

    private Product basketballUniform;
    private ProductStock redJersey;
    private ProductStock blackJersey;
    private ProductStock goldJersey;

    @BeforeEach
    void setUp() {
        // 상품 1: 축구 유니폼
        soccerUniform = productRepository.save(new Product("나이키", "축구 유니폼"));
        homeJersey = productStockRepository.save(new ProductStock(soccerUniform.getId(), "홈 저지", 89_000L, 1000L));
        awayJersey = productStockRepository.save(new ProductStock(soccerUniform.getId(), "어웨이 저지", 92_000L, 500L));

        // 상품 2: 농구 유니폼
        basketballUniform = productRepository.save(new Product("아디다스", "농구 유니폼"));
        redJersey = productStockRepository.save(new ProductStock(basketballUniform.getId(), "레드 저지", 110_000L, 1000L));
        blackJersey = productStockRepository.save(new ProductStock(basketballUniform.getId(), "블랙 저지", 115_000L, 500L));
        goldJersey = productStockRepository.save(new ProductStock(basketballUniform.getId(), "골드 저지", 119_000L, 150L));
    }

    @Nested
    @DisplayName("상품 조회")
    class FindProduct {

        @Test
        @DisplayName("상품 목록 조회 성공")
        void findAll() {
            ProductInfo.ProductList result = productService.findAll();
            // 👉 여기서 출력!
            System.out.println("조회된 상품 수: " + result.getProducts().size());
            result.getProducts().forEach(product -> {
                System.out.println("상품 이름: " + product.getName());
                System.out.println("브랜드: " + product.getBrand());
                System.out.println("옵션 수: " + product.getStocks().size());
                product.getStocks().forEach(stock -> {
                    System.out.println("- 옵션명: " + stock.getOptionValue() + ", 가격: " + stock.getPrice() + ", 재고: " + stock.getStock());
                });
            });
            assertThat(result.getProducts()).hasSize(2);

            ProductInfo.ProductDetail soccer = result.getProducts().get(0);
            assertThat(soccer.getBrand()).isEqualTo("나이키");
            assertThat(soccer.getName()).isEqualTo("축구 유니폼");

            assertThat(soccer.getStocks()).hasSize(2);
            assertThat(soccer.getStocks().get(0).getOptionValue()).isEqualTo("홈 저지");
            assertThat(soccer.getStocks().get(1).getOptionValue()).isEqualTo("어웨이 저지");

            ProductInfo.ProductDetail basketball = result.getProducts().get(1);
            assertThat(basketball.getBrand()).isEqualTo("아디다스");
            assertThat(basketball.getName()).isEqualTo("농구 유니폼");

            assertThat(basketball.getStocks()).hasSize(3);
            assertThat(basketball.getStocks().get(0).getOptionValue()).isEqualTo("레드 저지");
            assertThat(basketball.getStocks().get(1).getOptionValue()).isEqualTo("블랙 저지");
            assertThat(basketball.getStocks().get(2).getOptionValue()).isEqualTo("골드 저지");
        }

        @Test
        @DisplayName("상품 조회 실패 - 존재하지 않는 상품")
        void notFound() {
            ProductCommand.Find command = new ProductCommand.Find(999L);
            CustomException ex = assertThrows(CustomException.class, () -> productService.findProduct(command));
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("재고 차감")
    class ReduceStock {

        @Test
        @DisplayName("재고 차감 성공 - 충분한 경우만 차감")
        void success() {
            List<OrderCommand.OrderItem> command = List.of(
                    new OrderCommand.OrderItem(homeJersey.getId(), 89_000L, 1000L),  // 충분
                    new OrderCommand.OrderItem(awayJersey.getId(), 92_000L, 600L)    // 부족
            );

            ProductInfo.StockCheckResult result = productService.reduceStock(new OrderCommand.OrderItemList(command));

            assertThat(result.checkStocks()).hasSize(2);

            assertThat(result.checkStocks().get(0).isEnough()).isTrue();
            assertThat(result.checkStocks().get(0).remainingQuantity()).isEqualTo(0L);
            assertThat(result.checkStocks().get(0).requestQuantity()).isEqualTo(1000L);

            assertThat(result.checkStocks().get(1).isEnough()).isFalse();
            assertThat(result.checkStocks().get(1).remainingQuantity()).isEqualTo(500L);
            assertThat(result.checkStocks().get(1).requestQuantity()).isEqualTo(600L);

            assertThat(productStockRepository.findById(homeJersey.getId()).get().getStock()).isEqualTo(0L);
            assertThat(productStockRepository.findById(awayJersey.getId()).get().getStock()).isEqualTo(500L);
        }
    }
}
