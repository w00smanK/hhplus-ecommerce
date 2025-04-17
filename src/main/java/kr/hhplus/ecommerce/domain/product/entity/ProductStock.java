package kr.hhplus.ecommerce.domain.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductStock extends BaseEntity {
    @Id
    private Long id;
    private Long productId;
    private String optionValue;
    private Long price;
    private Long stock;

    @Builder
    public ProductStock(Long id, String optionValue, Long price, Long stock) {
        this.id = id;
        this.optionValue = optionValue;
        this.price = price;
        this.stock = stock;
    }

    public boolean canPurchase(Long stock) {
        return this.stock - stock >= 0;
    }

    public Long reduceStock(Long stock) {
        this.stock = this.stock - stock;

        if (this.stock < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        return this.stock;
    }

}
