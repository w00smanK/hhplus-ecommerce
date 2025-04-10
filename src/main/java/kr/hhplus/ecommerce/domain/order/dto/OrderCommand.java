package kr.hhplus.ecommerce.domain.order.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCommand {

    @Getter
    @Builder
    @RequiredArgsConstructor(staticName = "of")
    public static class Create {

        private final Long userId;
        private final Long userCouponId;
        private final double discountRate;
        private final List<OrderProduct> products;


    }

    @Getter
    @Builder
    @RequiredArgsConstructor(staticName = "of")
    public static class OrderProduct {

        private final Long productId;
        private final String productName;
        private final Long productPrice;
        private final int quantity;
    }

    @Getter
    public static class TopOrders {

        private final List<Long> orderIds;
        private final int limit;

        private TopOrders(List<Long> orderIds, int limit) {
            this.orderIds = orderIds;
            this.limit = limit;
        }

        public static TopOrders of(List<Long> orderIds, int limit) {
            return new TopOrders(orderIds, limit);
        }
    }
}
