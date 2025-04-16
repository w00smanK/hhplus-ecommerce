package kr.hhplus.ecommerce.domain.coupon.entity;

import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseEntity {

    private Long id;
    private Long discountPrice;
    private Integer quantity;

    public Integer issue() {
        return quantity--;
    }
}
