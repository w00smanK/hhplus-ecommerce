package kr.hhplus.ecommerce.domain.product.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductStockCommand {

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class OrderProducts {

        private final List<OrderProduct> products;
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class OrderProduct {

        private final Long productId;
        private final int quantity;

    }
}
