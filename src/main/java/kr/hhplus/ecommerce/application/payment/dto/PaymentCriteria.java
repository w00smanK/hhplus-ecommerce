package kr.hhplus.ecommerce.application.payment.dto;


import kr.hhplus.ecommerce.domain.payment.dto.PaymentCommand;

public record PaymentCriteria() {

    public record Pay(
            Long orderId,
            Long amount
    ) {
        public PaymentCommand.FindOrder toCommand() {
            return new PaymentCommand.FindOrder(orderId);
        }
    }
}
