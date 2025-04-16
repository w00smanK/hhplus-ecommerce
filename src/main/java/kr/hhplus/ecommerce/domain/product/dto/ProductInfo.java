package kr.hhplus.ecommerce.domain.product.dto;

import kr.hhplus.ecommerce.domain.product.entity.Product;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductInfo {

    @Getter
    @Builder
    @RequiredArgsConstructor(staticName = "of")
    public static class Products {

        private final List<ProductList> products;

    }

    @Getter
    @Builder
    @RequiredArgsConstructor(staticName = "of")
    public static class ProductList {

        private final Long productId;
        private final String productName;
        private final Long productPrice;
        private final int quantity;

    }

//    @Getter
//    @Builder
//    @RequiredArgsConstructor(staticName = "of")
//    public static class Products {
//
//        private final List<Product> products;
//    }

    @Getter
    @Builder
    @RequiredArgsConstructor(staticName = "of")
    public static class ProductDetail {

        private final Long productId;
        private final String productName;
        private final Long productPrice;

        public static ProductDetail from(Product product) {
            return ProductDetail.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productPrice(product.getPrice())
//                    .options(product.g)
                    .build();
        }
    }
}
