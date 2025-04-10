package kr.hhplus.ecommerce.domain.product.entity;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product  extends BaseEntity {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private long price;

    @Builder
    private Product(Long id, String name, long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public static Product create(String name, long price) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("상품 이름은 필수입니다.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("상품 가격은 0보다 커야 합니다.");
        }
        return Product.builder()
                .name(name)
                .price(price)
                .build();
    }
}
