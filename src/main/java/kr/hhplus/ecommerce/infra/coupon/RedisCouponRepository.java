package kr.hhplus.ecommerce.infra.coupon;

import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Redis를 이용한 쿠폰 저장소
 * Sorted Set을 이용하여 선착순 쿠폰 발급 기능 구현
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisCouponRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String COUPON_KEY_PREFIX = "coupon:";
    private static final String COUPON_ISSUED_KEY_PREFIX = "coupon:issued:";

    /**
     * 쿠폰 초기화 - 쿠폰 ID와 수량을 Redis에 저장
     * @param coupon 쿠폰 정보
     */
    public void initializeCoupon(Coupon coupon) {
        String couponKey = getCouponKey(coupon.getId());

        // 쿠폰이 이미 초기화되어 있는지 확인
        if (Boolean.FALSE.equals(redisTemplate.hasKey(couponKey))) {
            ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

            // 쿠폰 수량만큼 추가 (score는 0으로 동일하게 설정)
            for (int i = 0; i < coupon.getQuantity(); i++) {
                zSetOps.add(couponKey, String.valueOf(i), 0);
            }
        }
    }

    /**
     * 쿠폰 발급 - Redis의 Sorted Set에서 멤버 하나를 제거하고 발급 처리
     */
    public boolean issueCoupon(Long userId, Long couponId) {
        String couponKey = getCouponKey(couponId);
        String issuedKey = getIssuedKey(couponId, userId);

        log.info("쿠폰 발급 시도 - userId: {}, couponId: {}", userId, couponId);

        // 이미 발급받았는지 확인
        if (Boolean.TRUE.equals(redisTemplate.hasKey(issuedKey))) {
            log.warn("이미 발급받은 쿠폰 - userId: {}, couponId: {}", userId, couponId);
            return false;
        }

        // 쿠폰 재고 확인
        Long size = redisTemplate.opsForZSet().size(couponKey);
        log.info("쿠폰 재고 확인 - couponId: {}, size: {}", couponId, size);

        if (size == null || size <= 0) {
            log.warn("쿠폰 재고 부족 - couponId: {}", couponId);
            return false;
        }

        Set<ZSetOperations.TypedTuple<String>> poppedSet = redisTemplate.opsForZSet().popMin(couponKey, 1);

        if (poppedSet == null || poppedSet.isEmpty()) {
            log.warn("쿠폰 멤버 제거 실패 (이미 소진됨) - couponId: {}", couponId);
            return false;
        }

        ZSetOperations.TypedTuple<String> poppedItem = poppedSet.iterator().next();
        String member = poppedItem.getValue();

        log.info("쿠폰 멤버 제거 성공 - couponId: {}, member: {}", couponId, member);

        // 발급 정보 저장
        Boolean setResult = redisTemplate.opsForValue().setIfAbsent(issuedKey, "1");
        log.info("쿠폰 발급 정보 저장 - userId: {}, couponId: {}, result: {}", userId, couponId, setResult);

        // 발급 정보 저장에 실패했으면 쿠폰 복구
        if (Boolean.FALSE.equals(setResult)) {
            log.warn("쿠폰 발급 정보 저장 실패, 쿠폰 복구 - userId: {}, couponId: {}", userId, couponId);
            redisTemplate.opsForZSet().add(couponKey, member, poppedItem.getScore());
            return false;
        }

        log.info("쿠폰 발급 성공 - userId: {}, couponId: {}", userId, couponId);
        return true;
    }

    /**
     * 쿠폰 재고 확인
     */
    public long getCouponStock(Long couponId) {
        String couponKey = getCouponKey(couponId);
        Long size = redisTemplate.opsForZSet().size(couponKey);
        return size != null ? size : 0;
    }

    /**
     * 사용자의 쿠폰 발급 여부 확인
     */
    public boolean hasIssuedCoupon(Long userId, Long couponId) {
        String issuedKey = getIssuedKey(couponId, userId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(issuedKey));
    }

    /**
     * 쿠폰 발급 롤백 - 발급 실패 시 Redis에서 쿠폰을 다시 추가하고 발급 정보 삭제
     */
    public void rollbackIssuance(Long userId, Long couponId) {
        String couponKey = getCouponKey(couponId);
        String issuedKey = getIssuedKey(couponId, userId);

        // 발급 정보가 있는 경우에만 롤백 처리
        if (Boolean.TRUE.equals(redisTemplate.hasKey(issuedKey))) {
            // 쿠폰 재고 복구 (Sorted Set에 멤버 추가)
            ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
            zSetOps.add(couponKey, String.valueOf(System.nanoTime()), 0);

            // 발급 정보 삭제
            redisTemplate.delete(issuedKey);
        }
    }

    private String getCouponKey(Long couponId) {
        return COUPON_KEY_PREFIX + couponId;
    }

    private String getIssuedKey(Long couponId, Long userId) {
        return COUPON_ISSUED_KEY_PREFIX + couponId + ":" + userId;
    }
}
