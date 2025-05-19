package kr.hhplus.ecommerce.domain.rank.entity;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 인기 상품 랭킹 엔티티
 */
@Entity
@Table(name = "rank")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Rank extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "rank_date", nullable = false)
    private LocalDate rankDate;

    private Rank(Long productId, Long quantity, LocalDate rankDate) {
        this.productId = productId;
        this.quantity = quantity;
        this.rankDate = rankDate;
    }

    /**
     * 판매 랭킹 생성
     */
    public static Rank createSell(Long productId, LocalDate date, Long quantity) {
        return new Rank(productId, quantity, date);
    }
}