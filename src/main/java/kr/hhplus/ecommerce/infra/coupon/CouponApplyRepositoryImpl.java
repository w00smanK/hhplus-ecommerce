package kr.hhplus.ecommerce.infra.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponApplyRepository;
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
public class CouponApplyRepositoryImpl implements CouponApplyRepository {

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
    public boolean issueCouponEvent(Long userId, Long couponId) {
        String couponKey = getCouponKey(couponId);
        String issuedKey = getIssuedKey(couponId, userId);

        // 이미 발급받았는지 확인
        if (Boolean.TRUE.equals(redisTemplate.hasKey(issuedKey))) {
            return false;
        }

        // 재고에서 하나 꺼냄
        Set<ZSetOperations.TypedTuple<String>> popped = redisTemplate.opsForZSet().popMin(couponKey, 1);
        if (popped == null || popped.isEmpty()) {
            return false;
        }

        // 발급 기록 저장 (단순히 중복 방지를 위한 마킹)
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(issuedKey, "1"));
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

    @Override
    public boolean addToIssueQueue(Long userId, Long couponId) {
        String queueKey = getQueueKey(couponId);
        String issuedKey = getIssuedKey(couponId, userId);

        // 이미 발급받았거나 대기 중인지 확인
        if (Boolean.TRUE.equals(redisTemplate.hasKey(issuedKey))) {
            return false;
        }

        // 발급 대기 큐에 추가 (score는 현재 시간으로 선착순 보장)
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        Double score = (double) System.currentTimeMillis();
        Boolean added = zSetOps.add(queueKey, userId.toString(), score);
        
        if (Boolean.TRUE.equals(added)) {
            // 대기 상태 마킹
            redisTemplate.opsForValue().set(issuedKey, "PENDING");
            log.info("발급 대기 큐 추가 성공 - userId: {}, couponId: {}", userId, couponId);
        }
        
        return Boolean.TRUE.equals(added);
    }

    private String getCouponKey(Long couponId) {
        return COUPON_KEY_PREFIX + couponId;
    }

    private String getIssuedKey(Long couponId, Long userId) {
        return COUPON_ISSUED_KEY_PREFIX + couponId + ":" + userId;
    }

    private String getQueueKey(Long couponId) {
        return "coupon:queue:" + couponId;
    }

    // 개발/테스트용 메서드들
    /**
     * 개발/테스트용: 특정 쿠폰의 재고를 수동으로 설정
     */
    public void setManualCouponStock(Long couponId, int quantity) {
        String couponKey = getCouponKey(couponId);
        
        // 기존 쿠폰 데이터 삭제
        redisTemplate.delete(couponKey);
        
        // 새로운 쿠폰 재고 설정
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        long baseTime = System.currentTimeMillis();
        
        for (int i = 0; i < quantity; i++) {
            zSetOps.add(couponKey, "coupon:" + i, baseTime + i);
        }
        
        log.info("수동으로 쿠폰 재고 설정 완료 - couponId: {}, quantity: {}", couponId, quantity);
    }


    /**
     * 개발/테스트용: 모든 쿠폰 관련 Redis 데이터 삭제
     */
    public void clearAllCouponData(Long couponId) {
        String couponKey = getCouponKey(couponId);
        String queueKey = getQueueKey(couponId);
        String issuedPattern = COUPON_ISSUED_KEY_PREFIX + couponId + ":*";
        
        // 쿠폰 재고 데이터 삭제
        redisTemplate.delete(couponKey);
        
        // 대기 큐 삭제
        redisTemplate.delete(queueKey);
        
        // 발급 기록 삭제 (패턴 매칭으로 일괄 삭제)
        Set<String> keysToDelete = redisTemplate.keys(issuedPattern);
        if (keysToDelete != null && !keysToDelete.isEmpty()) {
            redisTemplate.delete(keysToDelete);
        }
        
        log.info("쿠폰 관련 모든 Redis 데이터 삭제 완료 - couponId: {}", couponId);
    }
}
