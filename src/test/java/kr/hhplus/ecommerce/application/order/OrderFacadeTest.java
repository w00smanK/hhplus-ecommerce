package kr.hhplus.ecommerce.application.order;

import kr.hhplus.ecommerce.application.order.dto.OrderCriteria;
import kr.hhplus.ecommerce.application.order.dto.OrderResult;
import kr.hhplus.ecommerce.concurrency.support.ConcurrentExecutor;
import kr.hhplus.ecommerce.domain.product.ProductRepository;
import kr.hhplus.ecommerce.domain.product.ProductStockRepository;
import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import kr.hhplus.ecommerce.domain.user.entity.User;
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

    private User user;
    private ProductStock iphone15_128GB;
    private ProductStock iphone15_256GB;
    private ProductStock galaxyS24_256GB;
    private ProductStock galaxyS24_512GB;

    @BeforeEach
    void setUp() {
        // 사용자 데이터 설정 - 김우경, 이림
        user = userRepository.findById(1L)
                .orElseGet(() -> userRepository.save(new User("김우경이림")));

        // 상품 옵션 데이터 조회
        iphone15_128GB = productStockRepository.findById(101L).orElseThrow();
        iphone15_256GB = productStockRepository.findById(102L).orElseThrow();
        galaxyS24_256GB = productStockRepository.findById(103L).orElseThrow();
        galaxyS24_512GB = productStockRepository.findById(104L).orElseThrow();

        // 재고 초기화 (테스트 데이터와 동일하게)
        // 기존 재고를 설정된 값으로 강제 설정
        // 이 방식은 테스트용으로만 사용하고, 실제 코드에서는 reduceStock 메서드를 사용해야 함
        try {
            // 리플렉션을 사용하여 private 필드 접근
            java.lang.reflect.Field stockField = ProductStock.class.getDeclaredField("stock");
            stockField.setAccessible(true);

            stockField.set(iphone15_128GB, 50L);
            stockField.set(iphone15_256GB, 30L);
            stockField.set(galaxyS24_256GB, 40L);
            stockField.set(galaxyS24_512GB, 20L);

            // 변경사항 저장
            productStockRepository.save(iphone15_128GB);
            productStockRepository.save(iphone15_256GB);
            productStockRepository.save(galaxyS24_256GB);
            productStockRepository.save(galaxyS24_512GB);
        } catch (Exception e) {
            throw new RuntimeException("테스트 데이터 설정 중 오류 발생", e);
        }
    }

    @Test
    @DisplayName("동시에 여러 건 주문 시 요청한 수에 맞는 재고를 차감한다.")
    void concurrentOrderShouldReduceStockCorrectly() throws InterruptedException {
        // given
        int threadCount = 10;
        int threadPoolSize = 5;
        Long productId = 1L; // iPhone 15
        Long optionId = 101L; // 128GB
        Long quantity = 2L; // 각 주문당 2개씩 주문

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // 초기 재고 확인
        ProductStock initialStock = productStockRepository.findById(optionId).orElseThrow();
        Long initialStockQuantity = initialStock.getStock();
        log.info("초기 재고: {}", initialStockQuantity);

        // 동시 주문 작업 생성
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                try {
                    // 주문 생성
                    OrderCriteria.Create criteria = new OrderCriteria.Create(
                            user.getId(),
                            productId,
                            List.of(new OrderCriteria.OrderItem(optionId, quantity)),
                            null // 쿠폰 없음
                    );

                    OrderResult.Create result = orderFacade.order(criteria);
                    log.info("주문 성공: orderId={}, 상태={}", result.orderId(), result.status());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("주문 실패: {}", e.getMessage());
                    failureCount.incrementAndGet();
                }
            });
        }

        // when
        ConcurrentExecutor.execute(threadPoolSize, threadCount, tasks);

        // then
        log.info("성공: {}, 실패: {}", successCount.get(), failureCount.get());

        // 최종 재고 확인
        ProductStock finalStock = productStockRepository.findById(optionId).orElseThrow();
        Long finalStockQuantity = finalStock.getStock();
        log.info("최종 재고: {}", finalStockQuantity);

        // 성공한 주문 수 * 주문 수량만큼 재고가 감소했는지 확인
        Long expectedStock = initialStockQuantity - (successCount.get() * quantity);
        assertThat(finalStockQuantity).isEqualTo(expectedStock);

        // 모든 요청이 처리되었는지 확인 (성공 + 실패 = 전체 요청 수)
        assertThat(successCount.get() + failureCount.get()).isEqualTo(threadCount);
    }
}
