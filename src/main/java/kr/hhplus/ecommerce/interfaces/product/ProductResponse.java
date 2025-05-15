package kr.hhplus.ecommerce.interfaces.product;

import kr.hhplus.ecommerce.application.product.dto.ProductResult;
import lombok.Builder;

import java.util.List;

public class ProductResponse {

    public record ProductList(List<ProductDetail> products) {
        public static ProductList from(ProductResult.ProductList productList) {
            List<ProductDetail> mapped = productList.products().stream()
                    .map(ProductDetail::from)
                    .toList();
            return new ProductList(mapped);
        }
    }

    @Builder
    public record ProductDetail(
            Long productId,
            String brand,
            String name,
            List<Option> options
    ) {
        public static ProductDetail from(ProductResult.ProductDetail product) {
            return ProductDetail.builder()
                    .productId(product.productId())
                    .brand(product.brand())
                    .name(product.name())
                    .options(product.options().stream().map(Option::from).toList())
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
        public static Option from(ProductResult.Option option) {
            return Option.builder()
                    .id(option.id())
                    .optionValue(option.optionValue())
                    .price(option.price())
                    .stock(option.stock())
                    .build();
        }
    }
}
