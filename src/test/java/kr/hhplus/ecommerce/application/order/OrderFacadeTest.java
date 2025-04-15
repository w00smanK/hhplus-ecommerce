//package kr.hhplus.ecommerce.application.order;
//
//import kr.hhplus.be.ecommerce.domain.balance.BalanceService;
//import kr.hhplus.be.ecommerce.domain.coupon.Coupon;
//import kr.hhplus.be.ecommerce.domain.coupon.CouponStatus;
//import kr.hhplus.be.ecommerce.domain.order.Order;
//import kr.hhplus.be.ecommerce.domain.order.OrderService;
//import kr.hhplus.be.ecommerce.domain.product.*;
//import kr.hhplus.be.ecommerce.domain.unit.Amount;
//import kr.hhplus.be.ecommerce.domain.unit.Quantity;
//import kr.hhplus.be.ecommerce.domain.unit.Rate;
//import kr.hhplus.be.ecommerce.domain.user.User;
//import kr.hhplus.be.ecommerce.domain.user.UserCoupon;
//import kr.hhplus.be.ecommerce.domain.user.UserCouponService;
//import kr.hhplus.be.ecommerce.domain.user.UserService;
//import kr.hhplus.be.ecommerce.support.MockTestSupport;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InOrder;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//class OrderFacadeTest extends MockTestSupport {
//
//    @InjectMocks
//    private OrderFacade orderFacade;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private ProductService productService;
//
//    @Mock
//    private UserCouponService userCouponService;
//
//    @Mock
//    private OrderService orderService;
//
//    @Mock
//    private BalanceService balanceService;
//
//    @Mock
//    private ProductStockService productStockService;
//
//    @DisplayName("주문 결제한다.")
//    @Test
//    void orderPayment() {
//        // given
//        OrderCriteria orderCriteria = OrderCriteria.of(
//            1L,
//            1L,
//            List.of(OrderCriteria.Product.of(1L, Quantity.of(1)))
//        );
//
//        User user = User.of("홍길동");
//
//        Coupon coupon = Coupon.builder()
//            .name("쿠폰명")
//            .discountRate(Rate.of(0.1))
//            .expiredAt(LocalDateTime.now().plusDays(1))
//            .status(CouponStatus.PUBLISHABLE)
//            .quantity(Quantity.of(3))
//            .build();
//        UserCoupon userCoupon = UserCoupon.of(user, coupon);
//
//        Product product1 = Product.of("상품명1", Amount.of(10_000L));
//        Product product2 = Product.of("상품명2", Amount.of(20_000L));
//
//        ProductQuantities productQuantities = ProductQuantities.of(List.of(
//            ProductQuantity.of(product1, Quantity.of(2)),
//            ProductQuantity.of(product2, Quantity.of(3))
//        ));
//
//        Order order = Order.create(user, userCoupon, productQuantities);
//
//        when(userService.getUser(anyLong()))
//            .thenReturn(user);
//
//        when(productService.getProductQuantities(any()))
//            .thenReturn(productQuantities);
//
//        when(userCouponService.getUsableCouponBy(anyLong(), anyLong()))
//            .thenReturn(userCoupon);
//
//        when(orderService.createOrder(any()))
//            .thenReturn(order);
//
//        // when
//        orderFacade.orderPayment(orderCriteria);
//
//        // then
//        InOrder inOrder = inOrder(
//            userService,
//            productService,
//            userCouponService,
//            orderService,
//            balanceService,
//            productStockService
//        );
//        inOrder.verify(userService, times(1)).getUser(anyLong());
//        inOrder.verify(productService, times(1)).getProductQuantities(any());
//        inOrder.verify(userCouponService, times(1)).getUsableCouponBy(anyLong(), anyLong());
//        inOrder.verify(orderService, times(1)).createOrder(any());
//
//        inOrder.verify(balanceService, times(1)).use(any());
//        inOrder.verify(userCouponService, times(1)).useCoupon(any());
//        inOrder.verify(productStockService, times(1)).deductStock(any());
//        inOrder.verify(orderService, times(1)).payOrder(any());
//        inOrder.verify(orderService, times(1)).sendOrderMessage(any());
//    }
//}