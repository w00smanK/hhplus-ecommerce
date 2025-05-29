package kr.hhplus.ecommerce.interfaces.order;

import kr.hhplus.ecommerce.domain.coupon.CouponEvent;
import kr.hhplus.ecommerce.domain.order.OrderEvent;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.product.ProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderService orderService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSendOrder(OrderEvent.OrderComplete event) {
        orderService.sendOrder(OrderCommand.Send.of(event));
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleUseCouponEvent(CouponEvent.UseCoupon event) {
        orderService.useCoupon(new OrderCommand.UseCoupon(event.orderId(), event.issuedCouponId(), event.userId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleHoldOrderEvent(ProductEvent.StockInsufficient event) {
        orderService.holdOrder(
                new OrderCommand.HoldOrder(
                        event.orderId(),
                        event.stockStatuses()
                )
        );
    }

}

