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
}
