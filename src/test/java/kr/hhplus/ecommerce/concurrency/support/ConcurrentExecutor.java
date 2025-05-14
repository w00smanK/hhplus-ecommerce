package kr.hhplus.ecommerce.concurrency.support;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentExecutor {
        public static void execute(int numberOfThreads, int counter, List<Runnable> tasks) throws InterruptedException {
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch startLatch = new CountDownLatch(1); // 동시에 시작하도록
            CountDownLatch completeLatch = new CountDownLatch(counter); // 완료 대기

            for (int i = 0; i < counter; i++) {
                Runnable task = tasks.get(i);
                int taskNumber = i + 1;

                executorService.execute(() -> {
                    try {
                        startLatch.await(); // 동시에 시작되도록 대기
                        task.run();
                    } catch (Exception e) {
                        System.err.printf("[ERROR] Task-%d 실패: %s%n", taskNumber, e.getMessage());
                    } finally {
                        completeLatch.countDown();
                    }
                });
            }

            System.out.println("[INFO] 모든 Task 준비 완료, 동시에 시작합니다.");
            startLatch.countDown(); // 동시에 시작
            completeLatch.await();  // 모든 작업 종료 대기
            executorService.shutdown();
    }
}
