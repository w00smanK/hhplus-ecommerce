package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.domain.product.dto.ProductStockCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductStockInfo;
import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import kr.hhplus.ecommerce.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductStockServiceTest extends MockTestSupport {

    @InjectMocks
    private ProductStockService stockService;

    @Mock
    private ProductStockRepository stockRepository;

    @DisplayName("유효한 ID로 재고를 차감해야 한다.")
    @Test
    void invalidIdStock() {
        // given
        ProductStockCommand.OrderProducts command = mock(ProductStockCommand.OrderProducts.class);
        ProductStockCommand.OrderProduct orderProduct = mock(ProductStockCommand.OrderProduct.class);

        when(command.getProducts())
            .thenReturn(List.of(orderProduct, orderProduct));

        when(stockRepository.findByProductId(anyLong()))
            .thenThrow(new IllegalArgumentException("재고가 존재하지 않습니다."));

        // when & then
        assertThatThrownBy(() -> stockService.lackStock(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 존재하지 않습니다.");
    }

    @DisplayName("재고가 있어야 차감할 수 있다. ")
    @Test
    void failWhenInsufficientStock() {
        // given
        ProductStockCommand.OrderProducts command = mock(ProductStockCommand.OrderProducts.class);
        ProductStockCommand.OrderProduct orderProduct = mock(ProductStockCommand.OrderProduct.class);

        when(orderProduct.getQuantity())
            .thenReturn(1);

        when(command.getProducts())
            .thenReturn(List.of(orderProduct, orderProduct));

        when(stockRepository.findByProductId(anyLong()))
            .thenReturn(ProductStock.create(1L, 0));

        // when
        assertThatThrownBy(() -> stockService.lackStock(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 부족합니다.");
    }

    @DisplayName("재고를 차감한다.")
    @Test
    void deductStock() {
        // given
        ProductStockCommand.OrderProducts command = mock(ProductStockCommand.OrderProducts.class);
        ProductStockCommand.OrderProduct orderProduct = mock(ProductStockCommand.OrderProduct.class);

        when(orderProduct.getQuantity())
            .thenReturn(10);

        when(command.getProducts())
            .thenReturn(List.of(orderProduct));

        ProductStock stock = ProductStock.create(1L, 10);
        when(stockRepository.findByProductId(anyLong()))
            .thenReturn(stock, stock);

        // when
        stockService.lackStock(command);

        // then
         assertThat(stock.getQuantity()).isZero();
    }

    @DisplayName("상품 ID로 재고를 조회한다.")
    @Test
    void getStock() {
        // given
        when(stockRepository.findByProductId(anyLong()))
            .thenReturn(ProductStock.create(1L, 10));

        // when
        ProductStockInfo.Stock stock = stockService.getStock(1L);

        // then
        assertThat(stock.getQuantity()).isEqualTo(10);
    }
}