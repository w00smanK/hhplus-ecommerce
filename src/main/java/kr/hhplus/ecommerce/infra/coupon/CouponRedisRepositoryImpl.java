package kr.hhplus.ecommerce.infra.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponRedisRepository;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CouponRedisRepositoryImpl implements CouponRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String COUPON_KEY_PREFIX = "coupon:";
    private static final String COUPON_ISSUED_KEY_PREFIX = "coupon:issued:";

    @Override
    public void initializeCoupon(Coupon coupon) {
        String couponKey = getCouponKey(coupon.getId());

        // 쿠폰이 이미 초기화되어 있는지 확인
        if (Boolean.FALSE.equals(redisTemplate.hasKey(couponKey))) {
            ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

            // 쿠폰 수량만큼 추가 (score는 현재 시간을 밀리초로 설정하여 선착순 순서 보장)
            long baseTime = System.currentTimeMillis();
            for (int i = 0; i < coupon.getQuantity(); i++) {
                zSetOps.add(couponKey, String.valueOf(i), baseTime + i);
            }
        }
    }

    @Override
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

        // 가장 오래된 쿠폰(가장 낮은 스코어)부터 발급 (선착순 순서 보장)
        Set<ZSetOperations.TypedTuple<String>> poppedSet = redisTemplate.opsForZSet().popMin(couponKey, 1);

        if (poppedSet == null || poppedSet.isEmpty()) {
            log.warn("쿠폰 멤버 제거 실패 (이미 소진됨) - couponId: {}", couponId);
            return false;
        }

        ZSetOperations.TypedTuple<String> poppedItem = poppedSet.iterator().next();
        String member = poppedItem.getValue();
        Double score = poppedItem.getScore();

        log.info("쿠폰 멤버 제거 성공 - couponId: {}, member: {}, 발급시각(score): {}", couponId, member, score);

        // 발급 정보 저장 (발급 시각을 값으로 저장)
        Boolean setResult = redisTemplate.opsForValue().setIfAbsent(issuedKey, String.valueOf(System.currentTimeMillis()));
        log.info("쿠폰 발급 정보 저장 - userId: {}, couponId: {}, result: {}", userId, couponId, setResult);

        // 발급 정보 저장에 실패했으면 쿠폰 복구
        if (Boolean.FALSE.equals(setResult)) {
            log.warn("쿠폰 발급 정보 저장 실패, 쿠폰 복구 - userId: {}, couponId: {}", userId, couponId);
            redisTemplate.opsForZSet().add(couponKey, member, score);
            return false;
        }

        log.info("쿠폰 발급 성공 - userId: {}, couponId: {}", userId, couponId);
        return true;
    }

    @Override
    public long getCouponStock(Long couponId) {
        String couponKey = getCouponKey(couponId);
        Long size = redisTemplate.opsForZSet().size(couponKey);
        return size != null ? size : 0;
    }

    @Override
    public boolean hasIssuedCoupon(Long userId, Long couponId) {
        String issuedKey = getIssuedKey(couponId, userId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(issuedKey));
    }

    @Override
    public void rollbackIssuance(Long userId, Long couponId) {
        String couponKey = getCouponKey(couponId);
        String issuedKey = getIssuedKey(couponId, userId);

        // 발급 정보가 있는 경우에만 롤백 처리
        if (Boolean.TRUE.equals(redisTemplate.hasKey(issuedKey))) {
            // 쿠폰 재고 복구 (Sorted Set에 멤버 추가)
            ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
            long currentTime = System.currentTimeMillis();
            String memberValue = String.valueOf(System.nanoTime());
            zSetOps.add(couponKey, memberValue, currentTime);

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
