package kr.hhplus.ecommerce.application.order;

import kr.hhplus.ecommerce.application.order.dto.OrderCriteria;
import kr.hhplus.ecommerce.application.order.dto.OrderResult;
import kr.hhplus.ecommerce.common.aop.annotation.DistributedLock;
import kr.hhplus.ecommerce.domain.coupon.CouponService;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponCommand;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponInfo;
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

    @DistributedLock(
            prefix = "order:stock",
            key = "#criteria.items[*].productOptionId",
            waitTime = 15,
            leaseTime = 5
    )
    @Transactional
    public OrderResult.Create order(OrderCriteria.Create criteria) {

        // 상품 조회
        ProductInfo.ProductDetail product = productService.findProduct(new ProductCommand.Find(criteria.productId()));
        List<OrderCommand.OrderItem> orderItemCommand = criteria.items().stream()
                .flatMap(item -> product.getStocks().stream()
                        .filter(option -> item.productOptionId().equals(option.getId()))
                        .map(option -> new OrderCommand.OrderItem(item.productOptionId(), option.getPrice(), item.quantity())))
                .toList();

        // 주문 생성
        OrderInfo.Create order = orderService.createOrder(new OrderCommand.Create(criteria.userId(), orderItemCommand));

        // 쿠폰 조회, 사용 처리
        CouponInfo.CouponStock couponInfo = couponService.use(new CouponCommand.Use(criteria.userId(), criteria.couponId()));

        // 쿠폰 적용
        orderService.useCoupon(OrderCommand.UseCoupon.toCommand(order.orderId(), couponInfo.couponId(), couponInfo.discountPrice()));

        // 재고 차감 -> 재고 부족시 해당 옵션 상태
        ProductInfo.StockCheckResult checkProductOrder = productService.reduceStock(new OrderCommand.OrderItemList(orderItemCommand));

        orderService.holdOrder(new OrderCommand.HoldOrder(order.orderId(), checkProductOrder.checkStocks()));

        //  결제 정보 저장
        paymentService.save(new PaymentCommand.Save(order.orderId(), order.paymentAmount()));

        return OrderResult.Create.from(order);
    }

//    @Async
//    @TransactionalEventListener(
//            phase = TransactionPhase.AFTER_COMMIT,
//            classes = OrderSuccessEvent.class
//    )
//    public void success(OrderSuccessEvent event) {
//        // 외부 플랫폼 데이터 전송
//        orderExternalClient.sendOrder(event);
//    }
//
//
//    @Transactional
//    public void success(long orderId) {
//        // 주문 ID로 주문 정보를 조회한다. 없으면 예외 발생
//        Order order = orderRepository.findOrderById(orderId)
//                .orElseThrow(() -> new BusinessException(BusinessError.ORDER_NOT_FOUND));
//
//        // 결제 성공 상태로 변경
//        order.success();
//
//        // 변경된 주문 상태를 저장
//        orderRepository.saveOrder(order);
//
//        // 주문 성공 이벤트 발행
//        orderEventPublisher.success(order);
//    }
}
