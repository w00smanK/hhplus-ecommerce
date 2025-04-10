package kr.hhplus.ecommerce.domain.product.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductInfo {

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
        private final String productName;
        private final Long productPrice;
        private final int quantity;

    }

    @Getter
    @Builder
    @RequiredArgsConstructor(staticName = "of")
    public static class Products {

        private final List<Product> products;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor(staticName = "of")
    public static class Product {

        private final Long productId;
        private final String productName;
        private final Long productPrice;
    }
}
