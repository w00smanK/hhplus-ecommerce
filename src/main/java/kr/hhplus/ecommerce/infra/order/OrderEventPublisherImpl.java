package kr.hhplus.ecommerce.infra.order;

import kr.hhplus.ecommerce.domain.order.OrderCompleteEvent;
import kr.hhplus.ecommerce.domain.order.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisherImpl implements OrderEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void complete(OrderCompleteEvent event) {
        log.info("주문 완료 이벤트 발행 - orderId: {}, userId: {}, paymentAmount: {}", 
                event.getOrderId(), event.getUserId(), event.getPaymentAmount());
        
        applicationEventPublisher.publishEvent(event);
    }

}
