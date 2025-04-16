package kr.hhplus.ecommerce.domain.product.entity;

import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStock extends BaseEntity {
    private Long id;
    private Long productId;
    private String optionValue;
    private Long price;
    private Integer stock;

    @Builder
    public ProductStock(Long id, String optionValue, Long price, Integer stock) {
        this.id = id;
        this.optionValue = optionValue;
        this.price = price;
        this.stock = stock;
    }

    public Integer reduceStock(Integer stock) {
        this.stock = this.stock - stock;

        if (this.stock < 0) {
            this.stock = 0;
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        return this.stock;
    }

}
