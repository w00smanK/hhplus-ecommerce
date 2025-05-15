package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.config.exception.CustomException;
import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponCommand;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponInfo;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.ecommerce.infra.coupon.RedisCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final RedisCouponRepository redisCouponRepository;


    // 선착순 쿠폰 단일쿠폰
    private static final Long FIRST_COME_COUPON_ID = 1L;
    // 선착순 쿠폰 수량
    private static final Integer FIRST_COME_COUPON_QUANTITY = 100;


    @Transactional
    public CouponInfo.CouponStock use(CouponCommand.Use command) {

        if (command.couponId() == null) {
            return CouponInfo.CouponStock.from();
        }
        Coupon coupon = couponRepository.findById(command.couponId())
//        Coupon coupon = couponRepository.findByIdWithLock(command.couponId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        IssuedCoupon issuedCoupon = issuedCouponRepository.findByUserIdAndCouponId(command.userId(), command.couponId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        issuedCoupon.use();

        return CouponInfo.CouponStock.from(coupon, issuedCoupon);
    }

    @Transactional
    public IssuedCoupon issue(CouponCommand.Issue command) {

        Coupon coupon = couponRepository.findById(command.couponId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        log.info("쿠폰 발급Service : {}", coupon.getId());

        if (coupon.getQuantity() <= 0) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        coupon.issue();

        return issuedCouponRepository.save(new IssuedCoupon(command.userId(), command.couponId()));
    }

    @Transactional
    public IssuedCoupon issueWithLock(CouponCommand.Issue command) {

        Coupon coupon = couponRepository.findByIdWithLock(command.couponId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (coupon.getQuantity() <= 0) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        coupon.issue();

        return issuedCouponRepository.save(new IssuedCoupon(command.userId(), command.couponId()));
    }

    /**
     * Redis Sorted Set을 이용한 선착순 쿠폰 발급
     */
    @Transactional
    public IssuedCoupon issueWithRedis(CouponCommand.Issue command) {
        try {
            Coupon coupon = couponRepository.findById(command.couponId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

            // 이미 발급받은 쿠폰인지 확인
            if (redisCouponRepository.hasIssuedCoupon(command.userId(), command.couponId())) {
                throw new CustomException(ErrorCode.DUPLICATE_COUPON);
            }

            // Redis를 통한 쿠폰 발급 시도
            boolean issued = redisCouponRepository.issueCoupon(command.userId(), command.couponId());
            if (!issued) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

            // DB에 발급 정보 저장
            IssuedCoupon issuedCoupon = issuedCouponRepository.save(new IssuedCoupon(command.userId(), command.couponId()));

            return issuedCoupon;
        } catch (Exception e) {
            // 발급 실패 시 Redis에서도 롤백
            redisCouponRepository.rollbackIssuance(command.userId(), command.couponId());
            throw e;
        }
    }

    @Transactional
    public IssuedCoupon save(CouponCommand.Save command) {

        issuedCouponRepository.findByUserIdAndCouponId(command.userId(), command.couponId())
                .ifPresent(coupon -> {
                    throw new CustomException(ErrorCode.BAD_REQUEST);
                });

        return issuedCouponRepository.save(new IssuedCoupon(command.userId(), command.couponId()));
    }

    /**
     * 일일 쿠폰 초기화
     * 매일 0시 00분에 100개의 쿠폰을 생성하고 Redis에 저장
     */
    @Transactional
    public Coupon initializeFirstComeCoupon() {
        Coupon newCoupon = Coupon.builder()
                .discountPrice(1000L)
                .quantity(FIRST_COME_COUPON_QUANTITY)
                .build();
        Coupon savedCoupon = couponRepository.save(newCoupon);

        // Redis에 초기화
        redisCouponRepository.initializeCoupon(savedCoupon);

        log.info("일일 쿠폰 초기화 완료 - couponId: {}, quantity: {}", savedCoupon.getId(), savedCoupon.getQuantity());

        return savedCoupon;
    }

    /**
     * 선착순 이벤트 종료 여부 확인
     */
    public boolean isEventEnded() {
        // Redis에서 남은 쿠폰 수량 확인
        long remainingStock = redisCouponRepository.getCouponStock(FIRST_COME_COUPON_ID);
        return remainingStock <= 0;
    }
}
