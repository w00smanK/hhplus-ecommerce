package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductStockTest {

    @DisplayName("재고 생성 시 수량이 0 미만이면 예외가 발생한다.")
    @Test
    void createWithNegativeQuantity_shouldThrowException() {
        // when & then
        assertThatThrownBy(() -> ProductStock.create(1001L, -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 수량은 0 이상이어야 합니다.");
    }

    @DisplayName("차감할 수량이 재고보다 많으면 예외가 발생한다.")
    @Test
    void deductMoreThanStock_shouldThrowException() {
        // given
        ProductStock stock = ProductStock.create(1002L, 5);
        int deductAmount = 10;

        // when & then
        assertThatThrownBy(() -> stock.lack(deductAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족합니다.");
    }

    @DisplayName("재고를 정상적으로 차감한다.")
    @Test
    void deductStock_successfully() {
        // given
        ProductStock stock = ProductStock.create(1003L, 10);

        // when
        stock.lack(4);

        // then
        assertThat(stock.getQuantity()).isEqualTo(6);
    }

    @DisplayName("재고를 모두 차감하면 0이 된다.")
    @Test
    void deductAllStock_toZero() {
        // given
        ProductStock stock = ProductStock.create(1004L, 3);

        // when
        stock.lack(3);

        // then
        assertThat(stock.getQuantity()).isZero();
    }
}
