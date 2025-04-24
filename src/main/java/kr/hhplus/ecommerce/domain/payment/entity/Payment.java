package kr.hhplus.ecommerce.domain.payment.entity;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor

@AllArgsConstructor
@Table(indexes = @Index(name = "idx_order_id", columnList = "orderId"))
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    private Long amount;

    @Column
    private LocalDateTime paidAt;

    public Payment(Long orderId, Long amount) {
        this.orderId = orderId;
        this.amount = amount;
        this.status = PaymentStatus.WAITING;
    }

    public Payment pay(Long amount) throws Exception {
        this.paidAt = LocalDateTime.now();
        this.amount -= amount;
        if (this.amount < 0) {
            throw new Exception(ErrorCode.BAD_REQUEST.getMessage());
        }
        if (this.amount == 0) {
            this.status = PaymentStatus.PAYED;
        }
        return this;
    }
}

