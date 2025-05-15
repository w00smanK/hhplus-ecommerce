package kr.hhplus.ecommerce.application.product.dto;

import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import lombok.Builder;

import java.util.List;

public record ProductResult() {

    public record ProductList(
            List<ProductDetail> products
    ) {
        public static ProductList from(ProductInfo.ProductList productInfo) {
            return new ProductList(productInfo.getProducts().stream().map(ProductDetail::from).toList());
        }
    }

    @Builder
    public record ProductDetail(
            Long productId,
            String brand,
            String name,
            List<Option> options
    ) {
        public static ProductDetail from(ProductInfo.ProductDetail product) {
            return ProductDetail.builder()
                    .productId(product.getProductId())
                    .brand(product.getBrand())
                    .name(product.getName())
                    .options(product.getStocks().stream().map(o -> Option.builder()
                                    .id(o.getId())
                                    .optionValue(o.getOptionValue())
                                    .price(o.getPrice())
                                    .stock(o.getStock())
                                    .build())
                            .toList())
                    .build();
        }
    }

    @Builder
    public record Option(
            Long id,
            String optionValue,
            Long price,
            Long stock
    ) {
        public static Option from(Option option) {
            return Option.builder()
                    .id(option.id())
                    .optionValue(option.optionValue())
                    .price(option.price())
                    .stock(option.stock())
                    .build();
        }
    }
}
