package kr.hhplus.ecommerce.domain.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.*;

@Getter
@AllArgsConstructor
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String name;

    @Builder
    public Product(String brand, String name) {
        this.brand = brand;
        this.name = name;
    }

    public static Product create(String name, Long price, ProductSellingStatus status) {
        Product product = new Product();
        product.name = name;
        product.brand = "nike"; // 테스트용 기본 브랜드
        return product;
    }
}
