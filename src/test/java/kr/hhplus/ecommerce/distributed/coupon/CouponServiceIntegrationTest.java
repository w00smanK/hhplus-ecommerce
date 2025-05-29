package kr.hhplus.ecommerce.distributed.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.ecommerce.domain.coupon.CouponService;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponRepository;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponCommand;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import kr.hhplus.ecommerce.domain.coupon.entity.CouponStatus;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("[통합테스트] CouponService")
@Transactional
class CouponServiceIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @Autowired
    private CouponService couponService;

    private Long USER_ID;
    private Long COUPON_ID;
    private Coupon COUPON;
    private IssuedCoupon ISSUED_COUPON;

    @BeforeEach
    void setUp() {
        USER_ID = 1L;
        //new Coupon(null, 500L, 10) → 왜 이게 중요할까?
        //JPA에서는 save(entity) 동작이 이렇게 달라:
        //ID 값	동작 방식
        //null	persist → 새로운 row 삽입 (INSERT)
        //있음 (ex. 1000L)	merge → 기존 row 덮어쓰기 (SELECT + UPDATE)
        COUPON = couponRepository.save(new Coupon(500L, 10));
        COUPON_ID = COUPON.getId();
        ISSUED_COUPON = issuedCouponRepository.save(new IssuedCoupon(USER_ID, COUPON_ID));
    }

    @Test
    @DisplayName("[성공] 쿠폰 적용시 상태 변경 (ISSUED -> USED)")
    void useCoupon_ok() {

        CouponCommand.Use command = new CouponCommand.Use(USER_ID, COUPON_ID,1L);

        couponService.use(command);

        IssuedCoupon actual = issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID).get();

        assertThat(actual.getStatus()).isEqualTo(CouponStatus.USED);
        assertThat(actual.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("[성공] 쿠폰 발급")
    void issue_ok() {


        Coupon newCoupon = couponRepository.save(new Coupon(500L,10));

        IssuedCoupon issuedCoupon = couponService.issue(new CouponCommand.Issue(USER_ID, newCoupon.getId()));

        Coupon coupon = couponRepository.findById(issuedCoupon.getCouponId()).get();
        assertThat(coupon.getQuantity()).isEqualTo(9L);

        IssuedCoupon actual = issuedCouponRepository.findByUserIdAndCouponId(USER_ID, issuedCoupon.getCouponId()).get();
        assertThat(actual.getCouponId()).isEqualTo(issuedCoupon.getCouponId());
        assertThat(actual.getUserId()).isEqualTo(USER_ID);
        assertThat(actual.getStatus()).isEqualTo(CouponStatus.ISSUED);
    }
}