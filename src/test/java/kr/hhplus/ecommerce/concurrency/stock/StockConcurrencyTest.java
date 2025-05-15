package kr.hhplus.ecommerce.concurrency.stock;

import kr.hhplus.ecommerce.concurrency.support.ConcurrentExecutor;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.product.ProductRepository;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.ProductStockRepository;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import kr.hhplus.ecommerce.domain.product.entity.Product;
import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@Testcontainers
@DisplayName("재고 동시성 테스트")
public class StockConcurrencyTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    private Product soccerUniform;
    private ProductStock homeJersey;

    @BeforeEach
    void beforeEach() {
        soccerUniform = productRepository.save(new Product("나이키", "축구 유니폼"));
        homeJersey = productStockRepository.save(new ProductStock(soccerUniform.getId(), "홈 저지", 89_000L, 80L));
    }

    @Test
    @DisplayName("[재고 차감] 재고 차감 - 비관적 락")
    void reduce_stock_concurrently() throws InterruptedException {

        int threadCount = 100;
        int threadPoolSize = 5;

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<Runnable> tasks = getRunnableList(threadCount, successCount, failureCount);

        // when
        ConcurrentExecutor.execute(threadPoolSize, threadCount, tasks);

        // then
        log.info("✅ 성공: {}, 실패: {}", successCount.get(), failureCount.get());
        assertThat(successCount.get() + failureCount.get()).isEqualTo(threadCount);

        ProductStock updatedStock = productStockRepository.findById(homeJersey.getId()).orElseThrow();
        log.info("✅ 재고: {}", updatedStock.getStock());
        assertThat(updatedStock.getStock()).isEqualTo(80 - successCount.get());
    }

    @NotNull
    private List<Runnable> getRunnableList(int threadCount, AtomicInteger successCount, AtomicInteger failureCount) {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                try {
                    List<OrderCommand.OrderItem> command = List.of(new OrderCommand.OrderItem(homeJersey.getId(), 1000L, 1L));
                    ProductInfo.StockCheckResult result = productService.reduceStock(new OrderCommand.OrderItemList(command));
                    if (result.checkStocks().get(0).isEnough()) {
                        successCount.incrementAndGet(); // 재고 차감 성공
                    } else {
                        failureCount.incrementAndGet(); // 재고 부족
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("[재고 차감 실패] {}", e.getMessage());
                }
            });
        }
        return tasks;
    }
}
