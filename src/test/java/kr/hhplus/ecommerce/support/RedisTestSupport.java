package kr.hhplus.ecommerce.support;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RedisTestSupport {
    @Container
    private static final GenericContainer<?> redisContainer =
            new GenericContainer<>("redis:7.2")
                    .withExposedPorts(6379);

    @BeforeAll
    void setUp() {
        System.setProperty("spring.redis.host", redisContainer.getHost());
        System.setProperty("spring.redis.port",
                redisContainer.getMappedPort(6379).toString());
    }
}
