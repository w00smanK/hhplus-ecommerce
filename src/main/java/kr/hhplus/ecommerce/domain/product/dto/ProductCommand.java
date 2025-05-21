package kr.hhplus.ecommerce.domain.product.dto;


import lombok.Getter;

import java.util.List;

public class ProductCommand {

    @Getter
    public static class Find {
        private final Long productId;

        public Find(Long productId) {
            this.productId = productId;
        }

    }

    public static class FindByProductOptionId {
        private final Long productOptionId;

        public FindByProductOptionId(Long productOptionId) {
            this.productOptionId = productOptionId;
        }

        public Long getProductOptionId() {
            return productOptionId;
        }
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
