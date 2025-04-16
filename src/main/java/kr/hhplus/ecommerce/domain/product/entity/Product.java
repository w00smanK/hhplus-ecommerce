package kr.hhplus.ecommerce.domain.product.entity;

import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Product extends BaseEntity {

    private Long id;
    private String brand;
    private String name;
}
