package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.order.entity.Order;
import kr.hhplus.ecommerce.domain.order.entity.OrderItem;
import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;
import kr.hhplus.ecommerce.domain.order.repository.OrderItemRepository;
import kr.hhplus.ecommerce.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 단위 테스트")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    private Long USER_ID;
    private Long COUPON_ID;
    private Long ORDER_ID;

    private OrderCommand.OrderItem ORDER_ITEM1;
    private OrderCommand.OrderItem ORDER_ITEM2;
    private List<OrderCommand.OrderItem> ORDER_ITEMS;

    @BeforeEach
    void setup() {
        USER_ID = 1L;
        COUPON_ID = 11L;
        ORDER_ID = 111L;

        ORDER_ITEM1 = new OrderCommand.OrderItem(1L, 10000L, 1);
        ORDER_ITEM2 = new OrderCommand.OrderItem(2L, 5000L, 2);
        ORDER_ITEMS = List.of(ORDER_ITEM1, ORDER_ITEM2);
    }

    @Test
    @DisplayName("주문 생성 - 쿠폰 없음")
    void createOrderWithoutCoupon() {
        Order order = Order.builder()
                .userId(USER_ID)
                .issuedCouponId(null)
                .totalAmount(20000L)
                .build();

        OrderCommand.Create command = OrderCommand.Create.builder()
                .userId(USER_ID)
                .issuedCouponId(null)
                .orderItems(ORDER_ITEMS)
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderInfo.Create actualInfo = orderService.createOrder(command);

        verify(orderRepository, times(1)).save(any(Order.class));

        assertThat(actualInfo.userId()).isEqualTo(USER_ID);
        assertThat(actualInfo.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(actualInfo.totalAmount()).isEqualTo(20000L);
        assertThat(actualInfo.discountAmount()).isEqualTo(0L);
        assertThat(actualInfo.paymentAmount()).isEqualTo(20000L);
    }

    @Test
    @DisplayName("상품 상태 HOLD 처리")
    void holdOrder() {
        Long productOptionId = 1L;
        OrderCommand.HoldOrder command = new OrderCommand.HoldOrder(productOptionId);

        OrderItem orderItem = OrderItem.builder()
                .orderId(1L)
                .productOptionId(1L)
                .unitPrice(1000L)
                .quantity(100)
                .build();

        when(orderItemRepository.findByProductOptionId(productOptionId)).thenReturn(Optional.of(orderItem));

        orderService.holdOrder(command);

        verify(orderItemRepository, times(1)).findByProductOptionId(productOptionId);
        assertEquals(OrderStatus.WAITING, orderItem.getStatus());
    }

    @Nested
    @DisplayName("쿠폰 적용")
    class UseCoupon {

        @Test
        @DisplayName("할인금액 < 주문금액")
        void applyCoupon_LessThanTotal() {
            Order order = Order.builder()
                    .userId(USER_ID)
                    .issuedCouponId(null)
                    .totalAmount(10000L)
                    .build();

            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(ORDER_ID, COUPON_ID, 3000L);

            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

            OrderInfo.Create actualInfo = orderService.useCoupon(command);

            assertThat(actualInfo.issuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actualInfo.totalAmount()).isEqualTo(10000L);
            assertThat(actualInfo.discountAmount()).isEqualTo(3000L);
            assertThat(actualInfo.paymentAmount()).isEqualTo(7000L);
        }

        @Test
        @DisplayName("할인금액 > 주문금액")
        void applyCoupon_GreaterThanTotal() {
            Order order = Order.builder()
                    .userId(USER_ID)
                    .issuedCouponId(null)
                    .totalAmount(10000L)
                    .build();

            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(ORDER_ID, COUPON_ID, 20000L);

            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

            OrderInfo.Create actualInfo = orderService.useCoupon(command);

            assertThat(actualInfo.issuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actualInfo.totalAmount()).isEqualTo(10000L);
            assertThat(actualInfo.discountAmount()).isEqualTo(10000L);
            assertThat(actualInfo.paymentAmount()).isEqualTo(0L);
        }

        @Test
        @DisplayName("할인금액 = 주문금액")
        void applyCoupon_EqualsTotal() {
            Order order = Order.builder()
                    .userId(USER_ID)
                    .issuedCouponId(null)
                    .totalAmount(10000L)
                    .build();

            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(ORDER_ID, COUPON_ID, 10000L);

            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

            OrderInfo.Create actualInfo = orderService.useCoupon(command);

            assertThat(actualInfo.issuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actualInfo.totalAmount()).isEqualTo(10000L);
            assertThat(actualInfo.discountAmount()).isEqualTo(10000L);
            assertThat(actualInfo.paymentAmount()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("주문 조회")
    class FindOrder {

        @Test
        @DisplayName("주문 조회 성공")
        void findByIdSuccess() {
            Order order = Order.builder()
                    .userId(USER_ID)
                    .issuedCouponId(COUPON_ID)
                    .totalAmount(10000L)
                    .build();

            order.pay();

            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

            Order actual = orderService.findById(new OrderCommand.Find(ORDER_ID));

            verify(orderRepository, times(1)).findById(ORDER_ID);
            assertThat(actual).isNotNull();
            assertThat(actual.getUserId()).isEqualTo(USER_ID);
            assertThat(actual.getIssuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actual.getTotalAmount()).isEqualTo(10000L);
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.PAYED);
        }

        @Test
        @DisplayName("주문 조회 실패 - 존재하지 않음")
        void findByIdNotFound() {
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            Exception exception = assertThrows(Exception.class,
                    () -> orderService.findById(new OrderCommand.Find(ORDER_ID)));

            verify(orderRepository).findById(ORDER_ID);
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.NOT_FOUND.getMessage());
        }
    }

    @Test
    @DisplayName("주문 결제 성공")
    void paySuccess() {
        Order order = Order.builder()
                .userId(USER_ID)
                .issuedCouponId(COUPON_ID)
                .totalAmount(10000L)
                .build();
        order.pay();

        Order mockOrder = mock(Order.class);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(mockOrder));
        when(mockOrder.pay()).thenReturn(order);

        Order actual = orderService.pay(new OrderCommand.Find(ORDER_ID));

        assertThat(actual).isNotNull();
        assertThat(actual.getUserId()).isEqualTo(USER_ID);
        assertThat(actual.getIssuedCouponId()).isEqualTo(COUPON_ID);
        assertThat(actual.getTotalAmount()).isEqualTo(10000L);
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.PAYED);

        verify(orderRepository).findById(ORDER_ID);
        verify(mockOrder).pay();
    }

    @Test
    @DisplayName("주문 결제 실패 - 주문 없음")
    void payNotFound() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class,
                () -> orderService.pay(new OrderCommand.Find(ORDER_ID)));

        verify(orderRepository).findById(ORDER_ID);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.NOT_FOUND.getMessage());
    }
}
