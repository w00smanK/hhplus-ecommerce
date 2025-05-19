package kr.hhplus.ecommerce.domain.rank.entity;

import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 인기 상품 랭킹 엔티티
 */
@Entity
@Table(name = "popular_rank")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PopularRank extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "rank_date", nullable = false)
    private LocalDate rankDate;

    public PopularRank(Long productId, Long quantity, LocalDate rankDate) {
        this.productId = productId;
        this.quantity = quantity;
        this.rankDate = rankDate;
    }

    public static PopularRank create(Long productId, Long quantity, LocalDate rankDate) {
        return new PopularRank(productId, quantity, rankDate);
    }
}
