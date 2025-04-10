package kr.hhplus.ecommerce.domain.payment.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCommand {

    @Getter
    @Builder
    @RequiredArgsConstructor(staticName = "of")
    public static class Payment {

        private final Long orderId;
        private final Long userId;
        private final long amount;
    }
}
