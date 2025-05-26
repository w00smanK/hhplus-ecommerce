package kr.hhplus.ecommerce.interfaces.product;

import kr.hhplus.ecommerce.common.aop.annotation.DistributedLock;
import kr.hhplus.ecommerce.domain.order.OrderEvent;
import kr.hhplus.ecommerce.domain.order.OrderEventPublisher;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventListener {
    
    private final ProductService productService;
    private final OrderService orderService;
    private final OrderEventPublisher eventPublisher;
    
    @EventListener
    @Transactional
    @DistributedLock(
        prefix = "order:stock",
        key = "#event.items[*].productOptionId",
        waitTime = 15,
        leaseTime = 5
    )
    public void handleOrderCreated(OrderEvent.OrderCreated event) {
        log.info("재고 차감 시작 - orderId: {}", event.orderId());
        
        try {
            // 이벤트에서 아이템 정보 가져오기
            List<OrderCommand.OrderItem> orderItems = event.items().stream()
                .map(item -> new OrderCommand.OrderItem(
                    item.productOptionId(),
                    item.price(),
                    item.quantity()
                ))
                .toList();
            
            // 재고 차감
            ProductInfo.StockCheckResult result = productService.reduceStock(
                new OrderCommand.OrderItemList(orderItems)
            );
            
            // 주문 상태 업데이트
            orderService.holdOrder(new OrderCommand.HoldOrder(
                event.orderId(),
                result.checkStocks()
            ));
            
            log.info("재고 차감 완료 - orderId: {}", event.orderId());
            
            // 결제 이벤트 발행
            eventPublisher.payOrder(new OrderEvent.OrderConfirmed(
                event.orderId(),
                event.userId(),
                event.paymentAmount()
            ));
            
        } catch (Exception e) {
            log.error("재고 차감 실패 - orderId: {}", event.orderId(), e);
            throw e;
        }
    }
}
