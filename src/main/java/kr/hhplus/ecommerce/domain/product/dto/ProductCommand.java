package kr.hhplus.ecommerce.domain.product.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductCommand {

    @Getter
    @Builder
    public static class findById {
        Long productId;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor(staticName = "of")
    public static class OrderProducts {

        private final List<OrderProduct> products;

    }

    @Getter
    @Builder
    @RequiredArgsConstructor(staticName = "of")
    public static class OrderProduct {

        private final Long productId;
        private final int quantity;

    }

    @Getter
    public static class Products {

        private final List<Long> productIds;

        private Products(List<Long> productIds) {
            this.productIds = productIds;
        }

        public static Products of(List<Long> productIds) {
            return new Products(productIds);
        }
    }
}
