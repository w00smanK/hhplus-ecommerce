package kr.hhplus.ecommerce.application.order;

import kr.hhplus.ecommerce.application.order.dto.OrderCriteria;
import kr.hhplus.ecommerce.application.order.dto.OrderResult;
import kr.hhplus.ecommerce.common.aop.annotation.DistributedLock;
import kr.hhplus.ecommerce.domain.coupon.CouponService;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponCommand;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponInfo;
import kr.hhplus.ecommerce.domain.order.OrderEvent;
import kr.hhplus.ecommerce.domain.order.OrderEventPublisher;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.payment.PaymentService;
import kr.hhplus.ecommerce.domain.payment.dto.PaymentCommand;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class OrderFacade {

    private final ProductService productService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final OrderEventPublisher eventPublisher;

    @DistributedLock(
            prefix = "order:stock",
            key = "#criteria.items[*].productOptionId",
            waitTime = 15,
            leaseTime = 5
    )
    @Transactional
    public OrderResult.Create order(OrderCriteria.Create criteria) {

        ProductInfo.ProductDetail product = productService.findProduct(new ProductCommand.Find(criteria.productId()));
        List<OrderCommand.OrderItem> orderItemCommand = criteria.items().stream()
                .flatMap(item -> product.getStocks().stream()
                        .filter(option -> item.productOptionId().equals(option.getId()))
                        .map(option -> new OrderCommand.OrderItem(item.productOptionId(), option.getPrice(), item.quantity())))
                .toList();

        OrderInfo.Create order = orderService.createOrder(new OrderCommand.Create(criteria.userId(), orderItemCommand));

        // 쿠폰 사용을 이벤트로 처리
        if (criteria.couponId() != null) {
            eventPublisher.publish(new OrderEvent.OrderCreated(
                order.orderId(),
                criteria.userId(),
                criteria.couponId(),
                order.paymentAmount()
            ));
        }

        ProductInfo.StockCheckResult checkProductOrder = productService.reduceStock(new OrderCommand.OrderItemList(orderItemCommand));

        orderService.holdOrder(new OrderCommand.HoldOrder(order.orderId(), checkProductOrder.checkStocks()));

        paymentService.save(new PaymentCommand.Save(order.orderId(), order.paymentAmount()));


        return OrderResult.Create.from(order);
    }

}
