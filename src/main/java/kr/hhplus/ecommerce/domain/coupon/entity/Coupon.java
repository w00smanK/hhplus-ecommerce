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

    public void issue() {
        if (quantity <= 0) throw new IllegalStateException("재고 부족");
        this.quantity = this.quantity - 1;
    }

    /**
     * 쿠폰 수량을 특정 값으로 설정
     * @param newQuantity 새로운 수량
     */
    public void updateQuantity(Integer newQuantity) {
        this.quantity = newQuantity;
    }
}
