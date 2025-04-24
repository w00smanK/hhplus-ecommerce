package kr.hhplus.ecommerce.interfaces.payment;


import kr.hhplus.ecommerce.application.payment.dto.PaymentCriteria;

public record PaymentRequest(
        Long orderId,
        Long amount
) {
    public PaymentCriteria.Pay toCriteria() {
        return new PaymentCriteria.Pay(orderId, amount);
    }
}
