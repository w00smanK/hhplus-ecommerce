package kr.hhplus.ecommerce.domain.product.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductStockInfo {

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class Stock {

        private final Long stockId;
        private final int quantity;

    }
}
