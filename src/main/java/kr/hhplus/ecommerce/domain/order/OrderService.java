package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.config.exception.CustomException;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.order.entity.Order;
import kr.hhplus.ecommerce.domain.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderEventPublisher orderEventPublisher;


    // 주문 오더 생성
    @Transactional
    public OrderInfo.Create createOrder(OrderCommand.Create command) {

        Long totalAmount = command.orderItems().stream()
                .mapToLong(item -> item.price() * item.quantity())
                .sum();
        // 주문 총 금액 계산
        Order order = new Order(command.userId(), totalAmount);
        Order savedOrder = orderRepository.save(order);

        command.orderItems().forEach(item -> orderItemRepository.save(
                new OrderItem(
                        savedOrder.getId(),
                        item.productOptionId(),
                        item.price(),
                        item.quantity()
                ))
        );

        orderEventPublisher.orderPublish(new OrderEvent.OrderCreated(
                savedOrder.getId(),
                savedOrder.getUserId(),
                command.couponId(),  // OrderCommand.Create에 couponId 추가 필요
                command.orderItems()
        ));

        return new OrderInfo.Create(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getIssuedCouponId(),
                savedOrder.getStatus(),
                savedOrder.getTotalAmount(),
                savedOrder.getDiscountAmount(),
                savedOrder.getPaymentAmount()
        );

    }

    @Transactional
    public void holdOrder(OrderCommand.HoldOrder command) {
        command.stockDetails().forEach(stock -> {
            if (!stock.isEnough()) {
                OrderItem orderItem = orderItemRepository.findByOrderAndOption(command.orderId(), stock.stockId());
                orderItem.holdStatus();
            }
        });
    }

    @Transactional
    public OrderInfo.Create useCoupon(OrderCommand.UseCoupon command) {

        if (command.couponId() == null) {
            return null;
        }

        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        order.useCoupon(command.couponId(), command.discountPrice());

        return new OrderInfo.Create(
                order.getId(),
                order.getUserId(),
                order.getIssuedCouponId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getPaymentAmount()
        );
    }

    @Transactional(readOnly = true)
    public Order findById(OrderCommand.Find command) {

        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        return order;
    }

    @Transactional
    public Order orderComplete(OrderCommand.Find command) {

        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        orderEventPublisher.orderComplete(OrderEvent.OrderComplete.from(order));
        return order.complete();
    }

    @Transactional(readOnly = true)
    public List<OrderInfo.Best> findBestSelling(OrderCommand.FindBest command) {
        return orderItemRepository.findBestSelling(command.days(), command.limit());
    }

    /**
     * 특정 날짜에 결제 완료된 상품 목록 조회
     */
    @Transactional(readOnly = true)
    public OrderInfo.PaidProducts getPaidProducts(OrderCommand.DateQuery command) {

        List<OrderInfo.PaidProduct> paidProducts = new java.util.ArrayList<>();

        // 더미데이터인듯
        paidProducts.add(OrderInfo.PaidProduct.of(1L, 10L));
        paidProducts.add(OrderInfo.PaidProduct.of(2L, 5L));
        paidProducts.add(OrderInfo.PaidProduct.of(3L, 3L));

        return OrderInfo.PaidProducts.of(paidProducts);
    }


    public void sendOrder(OrderCommand.Send commnad) {
        // 주문 정보 전송 비돟기 처리
        log.info("주문 정보 전송 비동기 처리");
    }
}
