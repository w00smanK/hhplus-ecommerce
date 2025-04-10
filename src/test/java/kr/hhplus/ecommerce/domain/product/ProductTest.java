package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @DisplayName("상품 생성 시, 이름은 필수이다.")
    @Test
    void createWithoutName() {
        // when & then
        assertThatThrownBy(() -> Product.create(null, 1000L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 이름은 필수입니다.");
    }

    @DisplayName("상품 생성 시, 가격은 0보다 커야 한다.")
    @Test
    void createWithInvalidPrice() {
        // when & then
        assertThatThrownBy(() -> Product.create("쿠폰명", 0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 가격은 0보다 커야 합니다.");
    }


}