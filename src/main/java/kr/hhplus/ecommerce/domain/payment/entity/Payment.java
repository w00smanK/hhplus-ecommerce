package kr.hhplus.ecommerce.domain.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private long amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private LocalDateTime paidAt;

    @Builder
    private Payment(Long id,
                    Long orderId,
                    long amount,
                    PaymentStatus paymentStatus,
                    LocalDateTime paidAt
                    ) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paidAt = paidAt;
    }

    public static Payment create( Long orderId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
        return Payment.builder()
            .orderId(orderId)
            .amount(amount)
                .paymentStatus(PaymentStatus.READY)
            .build();
    }

    public void pay() {
        if (paymentStatus.cannotPay()) {
            throw new IllegalStateException("결제 가능 상태가 아닙니다.");
        }

        this.paymentStatus = PaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
    }
}
