package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.config.exception.Exception;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.order.entity.Order;
import kr.hhplus.ecommerce.domain.order.entity.OrderItem;
import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderInfo.Create createOrder(OrderCommand.Create command) {

        Long totalAmount = command.orderItems().stream()
                .mapToLong(item -> item.unitPrice() * item.quantity())
                .sum();

        Order order = new Order(command.userId(), totalAmount);

        Order savedOrder = orderRepository.save(order);

        command.orderItems().forEach(item -> {
                    orderItemRepository.save(
                            new OrderItem(
                                    savedOrder.getId(),
                                    item.productOptionId(),
                                    item.unitPrice(),
                                    item.quantity()
                            ));
                }
        );

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

    @Transactional
    public void holdOrder(OrderCommand.HoldOrder command) {

        OrderItem orderItem = orderItemRepository.findByOrderIdAndProductOptionId(command.orderId(), command.productOptionId())
                .orElseThrow(() -> new Exception(ErrorCode.NOT_FOUND));

        orderItem.holdStatus();
    }

    @Transactional
    public OrderInfo.Create useCoupon(OrderCommand.UseCoupon command) {

        if (command.couponId() == null) {
            return null;
        }

        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new Exception(ErrorCode.NOT_FOUND));

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
                .orElseThrow(() -> new Exception(ErrorCode.NOT_FOUND));

        if (order.getStatus() != OrderStatus.PAYED) {
            throw new Exception(ErrorCode.BAD_REQUEST);
        }

        return order;
    }

    @Transactional
    public Order pay(OrderCommand.Find command) {

        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new Exception(ErrorCode.NOT_FOUND));

        return order.pay();
    }

    @Transactional(readOnly = true)
    public List<OrderInfo.Best> findBestSelling(OrderCommand.FindBest command) {
        return orderItemRepository.findBestSelling(command.days(), command.limit());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendOrder(OrderCommand.Send build) {
        // 주문 정보 전송 비돟기 처리
    }
}
