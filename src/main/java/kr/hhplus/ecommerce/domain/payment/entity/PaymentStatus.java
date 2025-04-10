package kr.hhplus.ecommerce.domain.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    READY("결제 준비"),
    COMPLETED("결제 완료"),
    FAILED("결제 실패"),
    CANCELLED("결제 취소"),
    ;

    private final String description;

    private static final Set<PaymentStatus> NON_PAYABLE_STATUSES =
            EnumSet.of(COMPLETED, FAILED, CANCELLED);

    public boolean cannotPay() {
        return NON_PAYABLE_STATUSES.contains(this);
    }

    public static List<PaymentStatus> completedOnly() {
        return List.of(COMPLETED);
    }
}
