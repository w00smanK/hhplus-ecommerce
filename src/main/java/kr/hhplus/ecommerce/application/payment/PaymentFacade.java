package kr.hhplus.ecommerce.application.payment;

import kr.hhplus.ecommerce.application.payment.dto.PaymentCriteria;
import kr.hhplus.ecommerce.application.payment.dto.PaymentResult;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.entity.Order;
import kr.hhplus.ecommerce.domain.payment.PaymentService;
import kr.hhplus.ecommerce.domain.payment.dto.PaymentCommand;
import kr.hhplus.ecommerce.domain.payment.entity.Payment;
import kr.hhplus.ecommerce.domain.point.PointService;
import kr.hhplus.ecommerce.domain.point.dto.PointCommand;
import kr.hhplus.ecommerce.domain.point.entity.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final PointService pointService;

    public PaymentResult.Pay pay(PaymentCriteria.Pay criteria) throws Exception {

        // 결제 찾기
        Payment payment = paymentService.findPayment(criteria.toCommand());

        // 주문 찾기
        Order order = orderService.findById(new OrderCommand.Find(payment.getOrderId()));

        // 결제금액 차감
        Point balance = pointService.reduce(new PointCommand.Reduce(order.getUserId(), criteria.amount(), order.getIssuedCouponId()));

        // 결제 완료
        Payment pay = paymentService.pay(new PaymentCommand.Pay(payment.getId(), order.getPaymentAmount()));

        // 주문 상태 변경
        order = orderService.pay(new OrderCommand.Find(payment.getOrderId()));

        // 주문 정보 전송
        orderService.sendOrder(
                new OrderCommand.Send(
                        order.getId(),
                        order.getUserId(),
                        order.getIssuedCouponId(),
                        order.getStatus(),
                        order.getPaymentAmount(),
                        order.getTotalAmount(),
                        order.getDiscountAmount()
                )
        );

        return new PaymentResult.Pay(
                pay.getId(),
                pay.getOrderId(),
                balance.getAccount(),
                order.getStatus(),
                pay.getStatus(),
                pay.getAmount(),
                pay.getPaidAt()
        );
    }
}
