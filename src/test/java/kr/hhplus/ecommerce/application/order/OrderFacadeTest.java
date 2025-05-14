package kr.hhplus.ecommerce.application.order;

import kr.hhplus.ecommerce.application.order.dto.OrderCriteria;
import kr.hhplus.ecommerce.concurrency.support.ConcurrentExecutor;
import kr.hhplus.ecommerce.domain.product.ProductRepository;
import kr.hhplus.ecommerce.domain.product.ProductStockRepository;
import kr.hhplus.ecommerce.domain.product.entity.Product;
import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import kr.hhplus.ecommerce.domain.user.entity.User;
import kr.hhplus.ecommerce.infra.order.OrderItemJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("OrderFacadeTest")
class OrderFacadeTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemJpaRepository orderItemRepository;

    private User user;
    private ProductStock iphone15_128GB;
    private ProductStock iphone15_256GB;
    private ProductStock galaxyS24_256GB;
    private ProductStock galaxyS24_512GB;

    @BeforeEach
    void setUp() {
        // 사용자 데이터 설정 - 김우경, 이림
        user = userRepository.findById(1L)
                .orElseGet(() -> userRepository.save(new User("김우경")));

        // 상품 및 옵션 등록
        Product iphone = productRepository.save(new Product("아이폰15", "Apple"));
        Product galaxy = productRepository.save(new Product("갤럭시S24", "Samsung"));

        iphone15_128GB = productStockRepository.save(new ProductStock(iphone.getId(), "128GB", 1000000L, 50L));
        iphone15_256GB = productStockRepository.save(new ProductStock(iphone.getId(), "256GB", 1200000L, 30L));
        galaxyS24_256GB = productStockRepository.save(new ProductStock(galaxy.getId(), "256GB", 1100000L, 40L));
        galaxyS24_512GB = productStockRepository.save(new ProductStock(galaxy.getId(), "512GB", 1300000L, 20L));
    }
    @BeforeEach
    void checkProxy() {
        log.info("orderFacade 클래스 확인: {}", orderFacade.getClass());
    }

    @Test
    @DisplayName("[분산락 동시성 테스트] ConcurrentExecutor로 재고 차감 테스트")
    void distributedLockConcurrencyTest() throws InterruptedException {
        // given
        List<OrderCriteria.Create> criteriaList = List.of(
                new OrderCriteria.Create(user.getId(), iphone15_128GB.getProductId(), List.of(
                        new OrderCriteria.OrderItem(iphone15_128GB.getId(), 1L),
                        new OrderCriteria.OrderItem(iphone15_256GB.getId(), 1L)
                ), null),
                new OrderCriteria.Create(user.getId(), iphone15_256GB.getProductId(), List.of(
                        new OrderCriteria.OrderItem(iphone15_256GB.getId(), 1L),
                        new OrderCriteria.OrderItem(galaxyS24_256GB.getId(), 1L)
                ), null),
                new OrderCriteria.Create(user.getId(), galaxyS24_256GB.getProductId(), List.of(
                        new OrderCriteria.OrderItem(iphone15_128GB.getId(), 1L),
                        new OrderCriteria.OrderItem(galaxyS24_256GB.getId(), 1L)
                ), null),
                new OrderCriteria.Create(user.getId(), galaxyS24_512GB.getProductId(), List.of(
                        new OrderCriteria.OrderItem(galaxyS24_512GB.getId(), 1L)
                ), null)
        );

        AtomicInteger successCount1 = new AtomicInteger();
        AtomicInteger successCount2 = new AtomicInteger();
        AtomicInteger successCount3 = new AtomicInteger();
        AtomicInteger successCount4 = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        List<Runnable> tasks = new ArrayList<>();
        int threadCount = 10;

        for (int i = 0; i < threadCount; i++) {
            int index = i % 4;
            tasks.add(() -> {
                try {
                    OrderCriteria.Create criteria = criteriaList.get(index);
                    log.info("🔁 [스레드 {}] 주문 요청 시작 - 옵션: {}", Thread.currentThread().getName(), criteria.items());
                    orderFacade.order(criteria);
                    switch (index) {
                        case 0 -> successCount1.incrementAndGet();
                        case 1 -> successCount2.incrementAndGet();
                        case 2 -> successCount3.incrementAndGet();
                        case 3 -> successCount4.incrementAndGet();
                    }
                    log.info("✅ [스레드 {}] 주문 성공", Thread.currentThread().getName());
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.warn("❌ [스레드 {}] 주문 실패: {}", Thread.currentThread().getName(), e.getMessage());
                }
            });
        }

        // when
        ConcurrentExecutor.execute(10, threadCount, tasks);

        // then
        ProductStock finalStock1 = productStockRepository.findById(iphone15_128GB.getId()).orElseThrow();
        ProductStock finalStock2 = productStockRepository.findById(iphone15_256GB.getId()).orElseThrow();
        ProductStock finalStock3 = productStockRepository.findById(galaxyS24_256GB.getId()).orElseThrow();
        ProductStock finalStock4 = productStockRepository.findById(galaxyS24_512GB.getId()).orElseThrow();

        long expected1 = 50 - (successCount1.get() + successCount3.get());
        long expected2 = 30 - (successCount1.get() + successCount2.get());
        long expected3 = 40 - (successCount2.get() + successCount3.get());
        long expected4 = 20 - successCount4.get();

        log.info("🧾 재고 결과 로그:");
        log.info("📦 iphone15_128GB | 예상: {} | 실제: {}", expected1, finalStock1.getStock());
        log.info("📦 iphone15_256GB | 예상: {} | 실제: {}", expected2, finalStock2.getStock());
        log.info("📦 galaxyS24_256GB | 예상: {} | 실제: {}", expected3, finalStock3.getStock());
        log.info("📦 galaxyS24_512GB | 예상: {} | 실제: {}", expected4, finalStock4.getStock());

        log.info("🎯 주문 결과 | 성공: {}, 실패: {}", successCount1.get() + successCount2.get() + successCount3.get() + successCount4.get(), failureCount.get());

        assertThat(finalStock1.getStock()).isEqualTo(expected1);
        assertThat(finalStock2.getStock()).isEqualTo(expected2);
        assertThat(finalStock3.getStock()).isEqualTo(expected3);
        assertThat(finalStock4.getStock()).isEqualTo(expected4);
    }
}
