package kr.hhplus.ecommerce.application.order;

import kr.hhplus.ecommerce.application.order.dto.OrderResult;
import kr.hhplus.ecommerce.domain.order.OrderEvent;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class OrderFacade {

    private final ProductService productService;

        ProductInfo.StockCheckResult checkProductOrder = productService.reduceStock(new OrderCommand.OrderItemList(orderItemCommand));

        orderService.holdOrder(new OrderCommand.HoldOrder(order.orderId(), checkProductOrder.checkStocks()));

        // 결제 정보 생성을 이벤트로 처리
        eventPublisher.payOrder(OrderEvent.OrderConfirmed.from(order, criteria.userId()));

        return OrderResult.Create.from(order);
    }

}
