package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import kr.hhplus.ecommerce.domain.product.entity.Product;
import kr.hhplus.ecommerce.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProductServiceTest extends MockTestSupport {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @DisplayName("존재하지 않는 상품 ID가 포함되면 예외가 발생한다.")
    @Test
    void getInvalidProductId() {
        // given
        ProductCommand.OrderProducts command = mock(ProductCommand.OrderProducts.class);
        ProductCommand.OrderProduct orderProduct = mock(ProductCommand.OrderProduct.class);

        when(command.getProducts())
                .thenReturn(List.of(orderProduct, orderProduct));

        when(productRepository.findById(anyLong()))
                .thenReturn(null);

        // when & then
        assertThatThrownBy(() -> productService.getOrderProducts(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품이 존재하지 않습니다.");
    }

    @DisplayName("주문 상품 목록을 정상적으로 반환한다.")
    @Test
    void getOrderProducts() {
        // given
        ProductCommand.OrderProducts command = mock(ProductCommand.OrderProducts.class);
        ProductCommand.OrderProduct orderProduct = mock(ProductCommand.OrderProduct.class);

        when(orderProduct.getQuantity()).thenReturn(2);
        when(command.getProducts()).thenReturn(List.of(orderProduct, orderProduct));

        when(productRepository.findById(anyLong()))
                .thenReturn(Product.builder()
                        .name("맥북 프로")
                        .price(2_500_000L)
                        .build());

        // when
        ProductInfo.OrderProducts result = productService.getOrderProducts(command);

        // then
        assertThat(result.getProducts()).hasSize(2)
                .extracting("productName", "productPrice", "quantity")
                .containsExactly(
                        tuple("맥북 프로", 2_500_000L, 2),
                        tuple("맥북 프로", 2_500_000L, 2)
                );
    }

    @DisplayName("상품 목록 조회 시 ID로 찾은 상품들을 반환한다.")
    @Test
    void getProducts() {
        // given
        ProductCommand.Products command = mock(ProductCommand.Products.class);

        List<Product> products = List.of(
                Product.create("아이폰", 100000L),
                Product.create("에어팟", 3000L),
                Product.create("애플워치", 50000L)
        );

        when(productRepository.findByIds(anyList()))
                .thenReturn(products);

        // when
        ProductInfo.Products result = productService.getProducts(command);

        // then
        assertThat(result.getProducts()).hasSize(3)
                .extracting("productName", "productPrice")
                .containsExactly(
                        tuple("아이폰", 100000L),
                        tuple("에어팟", 3000L),
                        tuple("애플워치", 50000L)
                );
    }
}
