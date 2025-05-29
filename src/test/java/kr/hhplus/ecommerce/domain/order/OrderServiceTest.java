package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.config.exception.CustomException;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponInfo;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.order.entity.Order;
import kr.hhplus.ecommerce.domain.order.entity.OrderItem;
import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
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
@DisplayName("주문")
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

        ORDER_ITEM1 = new OrderCommand.OrderItem(1L, 10000L, 1L);
        ORDER_ITEM2 = new OrderCommand.OrderItem(2L, 5000L, 2L);
        ORDER_ITEMS = List.of(ORDER_ITEM1, ORDER_ITEM2);
    }

    @Test
    @DisplayName("[성공] 주문 생성")
    void createOrder_hasNoCoupon_ok() {

        // Arrange
        Order order = new Order(USER_ID, 20000L);

        OrderCommand.Create command = new OrderCommand.Create(USER_ID,1L, ORDER_ITEMS);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderInfo.Create actualInfo = orderService.createOrder(command);

        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));

        assertThat(actualInfo.userId()).isEqualTo(USER_ID);
        assertThat(actualInfo.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(actualInfo.totalAmount()).isEqualTo(20000L);
        assertThat(actualInfo.discountAmount()).isEqualTo(0L);
        assertThat(actualInfo.paymentAmount()).isEqualTo(20000L);

    }

    @Test
    @DisplayName("[성공] 주문 후 상품 상태 변경 (CREATE -> PENDING)")
    void holdOrder() {

        // Arrange
        Long productOptionId = 1L;
        OrderItem orderItem = new OrderItem(1L, 1L, 1000L, 101L);
        List<ProductInfo.StockStatus> optionDetails = List.of(new ProductInfo.StockStatus(1L, false, 101L, 100L));

        OrderCommand.HoldOrder command = new OrderCommand.HoldOrder(1L, optionDetails);

        when(orderItemRepository.findByOrderAndOption(1L, productOptionId)).thenReturn(orderItem);

        // Act
        orderService.holdOrder(command);

        // Assert
        verify(orderItemRepository, times(1)).findByOrderAndOption(1L, productOptionId);
        assertEquals(OrderStatus.PENDING, orderItem.getStatus());
    }

    @Test
    @DisplayName("[성공] 인기 판매상품 조회")
    void findBestSelling_ok() {

        // Arrange
        when(orderItemRepository.findBestSelling(3, 5))
                .thenReturn(List.of(
                        new OrderInfo.Best(101L, 150L),
                        new OrderInfo.Best(102L, 120L),
                        new OrderInfo.Best(103L, 100L),
                        new OrderInfo.Best(104L, 80L),
                        new OrderInfo.Best(105L, 70L)
                ));

        // Act
        List<OrderInfo.Best> result = orderService.findBestSelling(new OrderCommand.FindBest(3, 5));

        // Assert
        verify(orderItemRepository).findBestSelling(3, 5);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(5);
    }

    @Nested
    @DisplayName("쿠폰 사용")
    class useCoupon {

        @Test
        @DisplayName("[성공] 쿠폰 미사용")
        void useCoupon_couponIsNull() {

            // Arrange
            CouponInfo.CouponStock couponInfo = new CouponInfo.CouponStock(null, null, null, null, null);
            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(ORDER_ID, couponInfo.couponId(), couponInfo.discountPrice());

            // Act
            OrderInfo.Create actualInfo = orderService.useCoupon(command);

            // Assert
            assertThat(actualInfo).isNull();
        }

        @Test
        @DisplayName("[성공] 쿠폰 적용 시 금액 계산 (주문금액 > 할인금액)")
        void useCoupon_totalAmountGtDiscountAmount() {

            // Arrange
            Order order = new Order(USER_ID, 10000L);

            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(ORDER_ID, COUPON_ID, 3000L);

            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

            // Act
            OrderInfo.Create actualInfo = orderService.useCoupon(command);

            // Assert
            assertThat(actualInfo.issuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actualInfo.totalAmount()).isEqualTo(10000L);
            assertThat(actualInfo.discountAmount()).isEqualTo(3000L);
            assertThat(actualInfo.paymentAmount()).isEqualTo(7000L);
        }

        @Test
        @DisplayName("[성공] 쿠폰 적용 시 금액 계산 (주문금액 < 할인금액)")
        void useCoupon_totalAmountLtDiscountAmount() {

            // Arrange
            Order order = new Order(USER_ID, 10000L);

            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(ORDER_ID, COUPON_ID, 20000L);

            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

            // Act
            OrderInfo.Create actualInfo = orderService.useCoupon(command);

            // Assert
            assertThat(actualInfo.issuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actualInfo.totalAmount()).isEqualTo(10000L);
            assertThat(actualInfo.discountAmount()).isEqualTo(10000L);
            assertThat(actualInfo.paymentAmount()).isEqualTo(0L);
        }

        @Test
        @DisplayName("[성공] 쿠폰 적용 시 금액 계산 (주문금액 < 할인금액)")
        void useCoupon_totalAmountEqDiscountAmount() {

            // Arrange
            Order order = new Order(USER_ID, 10000L);

            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(ORDER_ID, COUPON_ID, 10000L);

            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

            // Act
            OrderInfo.Create actualInfo = orderService.useCoupon(command);

            // Assert
            assertThat(actualInfo.issuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actualInfo.totalAmount()).isEqualTo(10000L);
            assertThat(actualInfo.discountAmount()).isEqualTo(10000L);
            assertThat(actualInfo.paymentAmount()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("주문 조회")
    class FindById {

        @Test
        @DisplayName("[실패] 주문 조회 -> 주문 없음(NOT_FOUND)")
        void findById_NotFound() {

            // Arrange
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            // Act
            CustomException customException = assertThrows(CustomException.class,
                    () -> orderService.findById(new OrderCommand.Find(ORDER_ID)));

            // Assert
            verify(orderRepository).findById(ORDER_ID);
            assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }
}