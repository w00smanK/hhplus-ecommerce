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

//    @Autowired
//    private OrderItemJpaRepository orderItemRepository;

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
        // 초기 재고 확인
        log.info("[DEBUG_LOG] Initial stock - iphone15_128GB: {}", iphone15_128GB.getStock());
        log.info("[DEBUG_LOG] Initial stock - iphone15_256GB: {}", iphone15_256GB.getStock());
        log.info("[DEBUG_LOG] Initial stock - galaxyS24_256GB: {}", galaxyS24_256GB.getStock());
        log.info("[DEBUG_LOG] Initial stock - galaxyS24_512GB: {}", galaxyS24_512GB.getStock());
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

        // 각 상품 옵션별 성공 카운트
        AtomicInteger iphone15_128GB_count = new AtomicInteger();
        AtomicInteger iphone15_256GB_count = new AtomicInteger();
        AtomicInteger galaxyS24_256GB_count = new AtomicInteger();
        AtomicInteger galaxyS24_512GB_count = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        List<Runnable> tasks = new ArrayList<>();
        int threadCount = 10;

        for (int i = 0; i < threadCount; i++) {
            final int idx = i % 4;
            tasks.add(() -> {
                try {
                    OrderCriteria.Create criteria = criteriaList.get(idx);
                    log.info("🔁 [스레드 {}] 주문 요청 시작 - 옵션: {}", Thread.currentThread().getName(), criteria.items());
                    orderFacade.order(criteria);

                    // 주문 성공 시 해당 상품 옵션의 카운트 증가
                    for (OrderCriteria.OrderItem item : criteria.items()) {
                        if (item.productOptionId().equals(iphone15_128GB.getId())) {
                            iphone15_128GB_count.incrementAndGet();
                            log.info("[DEBUG_LOG] Incremented iphone15_128GB_count: {}", iphone15_128GB_count.get());
                        } else if (item.productOptionId().equals(iphone15_256GB.getId())) {
                            iphone15_256GB_count.incrementAndGet();
                            log.info("[DEBUG_LOG] Incremented iphone15_256GB_count: {}", iphone15_256GB_count.get());
                        } else if (item.productOptionId().equals(galaxyS24_256GB.getId())) {
                            galaxyS24_256GB_count.incrementAndGet();
                            log.info("[DEBUG_LOG] Incremented galaxyS24_256GB_count: {}", galaxyS24_256GB_count.get());
                        } else if (item.productOptionId().equals(galaxyS24_512GB.getId())) {
                            galaxyS24_512GB_count.incrementAndGet();
                            log.info("[DEBUG_LOG] Incremented galaxyS24_512GB_count: {}", galaxyS24_512GB_count.get());
                        }
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

        // 각 상품 옵션별 주문 수량은 1이므로, 카운트 값이 곧 차감된 수량
        long expected1 = 50 - iphone15_128GB_count.get();
        long expected2 = 30 - iphone15_256GB_count.get();
        long expected3 = 40 - galaxyS24_256GB_count.get();
        long expected4 = 20 - galaxyS24_512GB_count.get();

        log.info("🧾 재고 결과 로그:");
        log.info("📦 iphone15_128GB | 예상: {} | 실제: {}", expected1, finalStock1.getStock());
        log.info("📦 iphone15_256GB | 예상: {} | 실제: {}", expected2, finalStock2.getStock());
        log.info("📦 galaxyS24_256GB | 예상: {} | 실제: {}", expected3, finalStock3.getStock());
        log.info("📦 galaxyS24_512GB | 예상: {} | 실제: {}", expected4, finalStock4.getStock());

        log.info("[DEBUG_LOG] Final counter values:");
        log.info("[DEBUG_LOG] iphone15_128GB_count: {}", iphone15_128GB_count.get());
        log.info("[DEBUG_LOG] iphone15_256GB_count: {}", iphone15_256GB_count.get());
        log.info("[DEBUG_LOG] galaxyS24_256GB_count: {}", galaxyS24_256GB_count.get());
        log.info("[DEBUG_LOG] galaxyS24_512GB_count: {}", galaxyS24_512GB_count.get());

        log.info("🎯 주문 결과 | 성공: {}, 실패: {}", iphone15_128GB_count.get() + iphone15_256GB_count.get() + galaxyS24_256GB_count.get() + galaxyS24_512GB_count.get(), failureCount.get());

        assertThat(finalStock1.getStock()).isEqualTo(expected1);
        assertThat(finalStock2.getStock()).isEqualTo(expected2);
        assertThat(finalStock3.getStock()).isEqualTo(expected3);
        assertThat(finalStock4.getStock()).isEqualTo(expected4);
    }
}
