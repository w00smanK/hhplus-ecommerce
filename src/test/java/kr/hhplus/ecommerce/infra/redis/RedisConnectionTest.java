package kr.hhplus.ecommerce.infra.redis;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class RedisConnectionTest {

     @Autowired
     private StringRedisTemplate redisTemplate;

     @Test
     void redisTest() {
         // given
         ValueOperations<String, String> ops = redisTemplate.opsForValue();
         String key = "test-key";
         String value = "hello, redis!";

         // when
         ops.set(key, value);
         String result = ops.get(key);

         // then
         Assertions.assertThat(result).isEqualTo(value);
     }
}
