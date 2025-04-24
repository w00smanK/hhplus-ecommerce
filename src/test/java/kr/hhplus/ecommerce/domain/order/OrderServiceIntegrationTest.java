package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.config.exception.Exception;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponInfo;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.entity.Order;
import kr.hhplus.ecommerce.domain.order.entity.OrderItem;
import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;
import kr.hhplus.ecommerce.domain.product.ProductRepository;
import kr.hhplus.ecommerce.domain.product.ProductStockRepository;
import kr.hhplus.ecommerce.domain.product.entity.Product;
import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("OrderService")
@Transactional(propagation = Propagation.NEVER)
class OrderServiceIntegrationTest {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductStockRepository productStockRepository;
    @Autowired
    OrderService orderService;

    Long userId;
    Long couponId;
    OrderCommand.OrderItem item1;
    OrderCommand.OrderItem item2;
    List<OrderCommand.OrderItem> items;

    @BeforeEach
    void setup() {
        userId = 1L;
        couponId = 11L;
        item1 = new OrderCommand.OrderItem(1L, 10000L, 1L);
        item2 = new OrderCommand.OrderItem(2L, 5000L, 2L);
        items = List.of(item1, item2);
    }

    @Test
    @DisplayName("create")
    void createOrder() {
        var command = new OrderCommand.Create(userId, items);
        var result = orderService.createOrder(command);
        var order = orderRepository.findById(result.orderId()).get();

        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getTotalAmount()).isEqualTo(20000L);
        assertThat(order.getDiscountAmount()).isEqualTo(0L);
        assertThat(order.getPaymentAmount()).isEqualTo(20000L);
        assertThat(orderItemRepository.findByOrderId(order.getId())).hasSize(2);
    }

    @Test
    @DisplayName("hold")
    void holdOrder() {
        var result = orderService.createOrder(new OrderCommand.Create(userId, items));
        var command = new OrderCommand.HoldOrder(result.orderId(), 1L);
        orderService.holdOrder(command);

        var orderItem = orderItemRepository.findByOrderIdAndProductStockId(result.orderId(), 1L).get();
        assertThat(orderItem.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("pay")
    void pay() {
        var order = orderRepository.save(new Order(userId, couponId, 10000L));
        var updated = orderService.pay(new OrderCommand.Find(order.getId()));

        var actual = orderRepository.findById(updated.getId()).get();
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.PAYED);
    }

    @Test
    @DisplayName("pay fail")
    void pay_fail() {
        var command = new OrderCommand.Find(9999L);
        var ex = assertThrows(Exception.class, () -> orderService.pay(command));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    @DisplayName("bestseller")
    void bestSelling() {
        var soju = productRepository.save(new Product("진로", "소주"));
        var sojuA = productStockRepository.save(new ProductStock(soju.getId(), "진로 이즈백", 3000L, 100L));
        var sojuB = productStockRepository.save(new ProductStock(soju.getId(), "진로 오리지널", 3500L, 200L));

        var beer = productRepository.save(new Product("카스", "맥주"));
        var beerA = productStockRepository.save(new ProductStock(beer.getId(), "카스 라이트", 4000L, 150L));
        var beerB = productStockRepository.save(new ProductStock(beer.getId(), "카스 프레시", 4500L, 250L));

        var order1 = orderRepository.save(new Order(userId, 0L));
        orderItemRepository.save(new OrderItem(order1.getId(), sojuA.getId(), 3000L, 20L));
        orderItemRepository.save(new OrderItem(order1.getId(), sojuB.getId(), 3500L, 15L));

        var order2 = orderRepository.save(new Order(userId, 0L));
        orderItemRepository.save(new OrderItem(order2.getId(), beerA.getId(), 4000L, 40L));
        orderItemRepository.save(new OrderItem(order2.getId(), beerB.getId(), 4500L, 35L));

        var result = orderService.findBestSelling(new OrderCommand.FindBest(2, 5));
        assertThat(result.size()).isEqualTo(5);
    }

    @Nested
    @DisplayName("coupon")
    class Coupon {

        @Test
        @DisplayName("null")
        void useCoupon_null() {
            var result = orderService.createOrder(new OrderCommand.Create(userId, items));
            var coupon = new CouponInfo.CouponAggregate(null, null, null, null, null);
            var command = new OrderCommand.UseCoupon(result.orderId(), coupon.couponId(), coupon.discountPrice());
            var actual = orderService.useCoupon(command);

            assertThat(actual).isNull();
        }

        @Test
        @DisplayName("valid")
        void useCoupon_valid() {
            var result = orderService.createOrder(new OrderCommand.Create(userId, items));
            var command = new OrderCommand.UseCoupon(result.orderId(), couponId, 3000L);
            var updated = orderService.useCoupon(command);
            var order = orderRepository.findById(updated.orderId()).get();

            assertThat(order.getIssuedCouponId()).isEqualTo(couponId);
            assertThat(order.getTotalAmount()).isEqualTo(20000L);
            assertThat(order.getDiscountAmount()).isEqualTo(3000L);
            assertThat(order.getPaymentAmount()).isEqualTo(17000L);
        }

        @Test
        @DisplayName("over discount")
        void useCoupon_overDiscount() {
            var result = orderService.createOrder(new OrderCommand.Create(userId, items));
            var command = new OrderCommand.UseCoupon(result.orderId(), couponId, 30000L);
            var updated = orderService.useCoupon(command);
            var order = orderRepository.findById(updated.orderId()).get();

            assertThat(order.getDiscountAmount()).isEqualTo(20000L);
            assertThat(order.getPaymentAmount()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("find")
    class Find {

        @Test
        @DisplayName("success")
        void findById() {
            var result = orderService.createOrder(new OrderCommand.Create(userId, items));
            var order = orderRepository.findById(result.orderId()).get();

            assertThat(order).isNotNull();
            assertThat(order.getUserId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("fail")
        void findById_fail() {
            var command = new OrderCommand.Find(999L);
            var ex = assertThrows(Exception.class, () -> orderService.findById(command));
            assertThat(ex.getMessage()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }
}
