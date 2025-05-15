package kr.hhplus.ecommerce.concurrency.support;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentExecutor {
        public static void execute(int numberOfThreads, int counter, List<Runnable> tasks) throws InterruptedException {
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch latch = new CountDownLatch(counter);

            // tasks가 counter보다 작을 수 있으니 index를 기준으로 실행
            for (int i = 0; i < counter; i++) {
                Runnable task = tasks.get(i);
                executorService.execute(() -> {
                    try {
                        task.run();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executorService.shutdown();
    }
}
