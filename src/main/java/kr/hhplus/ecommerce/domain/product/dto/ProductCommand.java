package kr.hhplus.ecommerce.domain.product.dto;


public class ProductCommand {

    public static class Find {
        private final Long productId;

        public Find(Long productId) {
            this.productId = productId;
        }

        public Long getProductId() {
            return productId;
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
