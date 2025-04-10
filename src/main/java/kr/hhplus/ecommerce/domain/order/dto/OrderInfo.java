package kr.hhplus.ecommerce.domain.order.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderInfo {

    @Getter
    @Builder
    @RequiredArgsConstructor(staticName = "of")
    public static class Order {

        private final Long orderId;
        private final long totalPrice;
        private final long discountPrice;
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class TopPaidProducts {

        private final List<Long> productIds;

    }
}
