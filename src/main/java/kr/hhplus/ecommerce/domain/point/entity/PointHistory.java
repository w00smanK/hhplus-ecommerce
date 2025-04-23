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
    @Column(name = "point_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private long pointId;
    @Column
    private Long issuedCouponId;
    @Column
    private long amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    public PointHistory(Long pointId, Long amount, TransactionType type) {
        this.pointId = pointId;
        this.amount = amount;
        this.transactionType = type;
    }

    public PointHistory(Long pointId, Long issuedCouponId, Long amount, TransactionType type) {
        this.pointId = pointId;
        this.issuedCouponId = issuedCouponId;
        this.amount = amount;
        this.transactionType = type;
    }

}
