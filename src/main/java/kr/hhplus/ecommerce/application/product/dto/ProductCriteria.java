package kr.hhplus.ecommerce.application.product.dto;

import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;

public record ProductCriteria() {

    public record Find(
            Long productId
    ) {
        public ProductCommand.Find toCommand(Long productId) {
            return new ProductCommand.Find(productId);
        }
    }

}
