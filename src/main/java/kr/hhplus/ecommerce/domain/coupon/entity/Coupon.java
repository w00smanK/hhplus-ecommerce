package kr.hhplus.ecommerce.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coupon") // 필요에 따라 생략 가능
public class Coupon extends BaseEntity {

    @Id
    @Column(name = "coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment 사용 시
    private Long id;

    @Column(nullable = false)
    private Long discountPrice;

    @Column(nullable = false)
    private Integer quantity;

    public Integer issue() {
        return quantity--;
    }
}
