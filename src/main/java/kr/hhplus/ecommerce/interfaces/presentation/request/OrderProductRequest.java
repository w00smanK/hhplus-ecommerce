package kr.hhplus.ecommerce.interfaces.presentation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderProductRequest {

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long id;

    @NotNull(message = "상품 구매 수량은 필수입니다.")
    private Integer quantity;

    private OrderProductRequest(Long id, Integer quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public static OrderProductRequest of(Long id, Integer quantity) {
        return new OrderProductRequest(id, quantity);
    }
}
