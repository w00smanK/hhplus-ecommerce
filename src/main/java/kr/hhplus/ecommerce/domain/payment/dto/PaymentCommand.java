package kr.hhplus.ecommerce.domain.payment.dto;

public record PaymentCommand() {


    public record Save(
            Long orderId,
            Long amount
    ) {
    }

    public record FindOrder(
            Long orderId
    ) {
    }


    public record Pay(
            Long paymentId,
            Long paymentAmount
    ) {
    }
}
