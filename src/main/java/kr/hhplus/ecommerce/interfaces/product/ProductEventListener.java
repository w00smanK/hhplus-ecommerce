package kr.hhplus.ecommerce.interfaces.product;

import kr.hhplus.ecommerce.domain.order.OrderEvent;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventListener {

    private final ProductService productService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void use(OrderEvent.OrderCreated event) {
        OrderCommand.ReduceStock command = new OrderCommand.ReduceStock(
                event.orderId(),
                event.orderItems()
        );

        productService.reduceStock(command);
    }
}
