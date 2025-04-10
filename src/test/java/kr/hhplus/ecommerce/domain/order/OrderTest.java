package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.domain.order.entity.Order;
import kr.hhplus.ecommerce.domain.order.entity.OrderProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @DisplayName("주문 상품이 없는 주문을 생성할 수 없다.")
    @Test
    void emptyOrder() {
        // when & then
        assertThatThrownBy(() -> Order.create(1L, null, 0.0, List.of()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문 상품이 없습니다.");
    }

    @DisplayName("주문")
    @Test
    void Order() {
        // given
        List<OrderProduct> orderProducts = List.of(
                OrderProduct.create(1L, "노트북", 1500000, 1),   // 150만
                OrderProduct.create(2L, "마우스", 30000, 2),     // 6만
                OrderProduct.create(3L, "모니터", 400000, 1),    // 40만
                OrderProduct.create(4L, "키보드", 80000, 1)      // 8만
        );

        long expectedTotal = 1_500_000 + 30_000 * 2 + 400_000 + 80_000; // = 2,040,000

        // when
        Order order = Order.create(1L, null, 0.0, orderProducts);

        // then
        assertThat(order.getDiscountPrice()).isZero();
        assertThat(order.getTotalPrice()).isEqualTo(expectedTotal);
    }

    @DisplayName("20% 할인이 적용된 주문")
    @Test
    void discountOrder() {
        // given
        List<OrderProduct> orderProducts = List.of(
                OrderProduct.create(1L, "상품A", 1500, 2),
                OrderProduct.create(2L, "상품B", 2500, 1),
                OrderProduct.create(3L, "상품C", 1200, 4)
        );

        double discountRate = 0.2;
        long expectedTotal = 1500 * 2 + 2500 + 1200 * 4; // 10,300
        long expectedDiscount = (long) (expectedTotal * discountRate);
        long expectedFinalPrice = expectedTotal - expectedDiscount;

        // when
        Order order = Order.create(1L, 99L, discountRate, orderProducts);

        // then
        assertThat(order.getDiscountPrice()).isEqualTo(expectedDiscount);
        assertThat(order.getTotalPrice()).isEqualTo(expectedFinalPrice);
    }

}