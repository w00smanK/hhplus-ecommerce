package kr.hhplus.ecommerce.interfaces.product;


import kr.hhplus.ecommerce.application.product.dto.ProductCriteria;

public record ProductRequest() {

    public record Find(
            Long productId
    ) {
        public static ProductCriteria.Find toCriteria(Long productId) {
            return new ProductCriteria.Find(productId);
        }
    }

}
