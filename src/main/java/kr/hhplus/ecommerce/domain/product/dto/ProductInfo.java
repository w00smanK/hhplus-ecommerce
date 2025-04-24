package kr.hhplus.ecommerce.domain.product.dto;

import kr.hhplus.ecommerce.domain.product.entity.Product;
import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductInfo {

    @Getter
    public static class ProductList {
        private final List<ProductDetail> products;

        public ProductList(List<ProductDetail> products) {
            this.products = products;
        }

        public static ProductList of(List<ProductDetail> details) {
            return new ProductList(details);
        }
    }

    @Getter
    @Builder
    public static class ProductDetail {
        private final Long productId;
        private final String brand;
        private final String name;
        private final List<ProductStock> stocks;

        public static ProductDetail from(Product product, List<ProductStock> stocks) {
            List<ProductStock> copied = stocks.stream()
                    .map(s -> new ProductStock(s.getId(), s.getOptionValue(), s.getPrice(), s.getStock()))
                    .collect(Collectors.toList());

            return ProductDetail.builder()
                    .productId(product.getId())
                    .brand(product.getBrand())
                    .name(product.getName())
                    .stocks(copied)
                    .build();
        }
    }


    public record StockStatus(
            Long stockId,
            boolean isEnough,
            Long requestQuantity,
            Long remainingQuantity
    ) {
    }

    public record StockCheckResult(List<StockStatus> checkStocks) {
    }
}
