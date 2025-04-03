package kr.hhplus.ecommerce.interfaces.presentation.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OrderRequest {

    @NotNull(message = "사용자 ID는 필수 입니다.")
    private Long userId;
    private Long couponId;

    @Valid
    @NotEmpty(message = "상품 목록은 1개 이상이여야 합니다.")
    private List<OrderProductRequest> products;

    private OrderRequest(Long userId, Long couponId, List<OrderProductRequest> products) {
        this.userId = userId;
        this.couponId = couponId;
        this.products = products;
    }

    public static OrderRequest of(Long userId, Long couponId, List<OrderProductRequest> products) {
        return new OrderRequest(userId, couponId, products);
    }
}
