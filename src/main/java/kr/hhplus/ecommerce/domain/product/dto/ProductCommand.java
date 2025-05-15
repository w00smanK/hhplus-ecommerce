package kr.hhplus.ecommerce.domain.product.dto;


import lombok.Getter;

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
}
