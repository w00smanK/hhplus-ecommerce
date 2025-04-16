package kr.hhplus.ecommerce.application.product.dto;

import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResult {

    @Getter
    public static class Products {

        private final List<Product> products;

        private Products(List<Product> products) {
            this.products = products;
        }

        public static Products of(List<Product> products) {
            return new Products(products);
        }
    }

    @Getter
    @Builder
    public static class Product {

        private final Long productId;
        private final String productName;
        private final Long productPrice;
//        private final int quantity;

        public static Product from(ProductInfo.ProductDetail productInfo) {
            return Product.builder()
                .productId(productInfo.getProductId())
                .productName(productInfo.getProductName())
                .productPrice(productInfo.getProductPrice())
//                .quantity(quantity)
                .build();
        }
    }
}
