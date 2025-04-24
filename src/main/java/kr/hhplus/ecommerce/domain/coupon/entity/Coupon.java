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
@Table(name = "coupon")
public class Coupon extends BaseEntity {

    @Id
    @Column(name = "coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long discountPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Version
    Long version;
    public Coupon(Long discountPrice, Integer quantity) {
        this.discountPrice = discountPrice;
        this.quantity = quantity;
    }
    public Integer issue() {
        return quantity--;
    }
}
