package kr.hhplus.ecommerce.domain.point.entity;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PointHistory extends BaseEntity {

    public static Object TransactionType;
    @Id
    @Column(name = "point_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private long userId;
    @Column
    private Long issuedCouponId;
    @Column
    private long amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    public PointHistory(Long userId, Long amount, TransactionType type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
    }

    public PointHistory(Long userId, Long issuedCouponId, Long amount, TransactionType transactionType) {
        this.userId = userId;
        this.issuedCouponId = issuedCouponId;
        this.amount = amount;
        this.transactionType = transactionType;
    }

}
