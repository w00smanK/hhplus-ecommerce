package kr.hhplus.ecommerce.domain.rank.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "product_rank")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rank {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private Long quantity;
    
    @Column(nullable = false)
    private LocalDate rankDate;
    
    private Rank(Long productId, Long quantity, LocalDate rankDate) {
        this.productId = productId;
        this.quantity = quantity;
        this.rankDate = rankDate;
    }
    
    public static Rank create(Long productId, Long quantity, LocalDate rankDate) {
        return new Rank(productId, quantity, rankDate);
    }
}