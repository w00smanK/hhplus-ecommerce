package kr.hhplus.ecommerce.application.coupon;

import kr.hhplus.ecommerce.application.coupon.dto.CouponCriteria;
import kr.hhplus.ecommerce.application.coupon.dto.CouponResult;
import kr.hhplus.ecommerce.common.aop.annotation.DistributedLock;
import kr.hhplus.ecommerce.domain.coupon.CouponService;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponFacade {

    private final CouponService couponService;

    @DistributedLock(
            prefix = "coupon:issue",
            key = "#criteria.couponId",
            waitTime = 10,
            leaseTime = 3
    )
    public CouponResult.Issued couponFirstIssue(CouponCriteria.Issue criteria) {

        IssuedCoupon issuedCoupon = couponService.issueWithLock(criteria.toCommand());

        return CouponResult.Issued.builder()
                .id(issuedCoupon.getId())
                .userId(issuedCoupon.getUserId())
                .couponId(issuedCoupon.getCouponId())
                .status(issuedCoupon.getStatus())
                .expiredAt(issuedCoupon.getExpiredAt())
                .build();
    }

    /**
     * Redis Sorted Set을 이용한 선착순 쿠폰 발급
     * Redis가 동시성을 처리하므로 별도의 분산 락이 필요 없음
     */
    public CouponResult.Issued couponFirstIssueWithRedis(CouponCriteria.Issue criteria) {
        IssuedCoupon issuedCoupon = couponService.issueWithRedis(criteria.toCommand());

        return CouponResult.Issued.builder()
                .id(issuedCoupon.getId())
                .userId(issuedCoupon.getUserId())
                .couponId(issuedCoupon.getCouponId())
                .status(issuedCoupon.getStatus())
                .expiredAt(issuedCoupon.getExpiredAt())
                .build();
    }


    /**
     * 일일 쿠폰 초기화
     * 매일 0시 00분에 100개의 쿠폰을 생성하고 Redis에 저장
     * @return 생성된 쿠폰 정보
     */
    public CouponResult.Info initializeFirstComeCoupon() {
        Coupon coupon = couponService.initializeFirstComeCoupon();
        log.info("일일 쿠폰 초기화 - couponId: {}, quantity: {}", coupon.getId(), coupon.getQuantity());

        return CouponResult.Info.builder()
                .id(coupon.getId())
                .discountPrice(coupon.getDiscountPrice())
                .quantity(coupon.getQuantity())
                .build();
    }

    /**
     * 선착순 이벤트 종료 여부 확인
     * @return 이벤트 종료 여부
     */
    public boolean isEventEnded() {
        return couponService.isEventEnded();
    }
}
