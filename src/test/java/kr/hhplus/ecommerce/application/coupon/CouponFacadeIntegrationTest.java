package kr.hhplus.ecommerce.application.coupon;

import kr.hhplus.ecommerce.application.coupon.dto.CouponCriteria;
import kr.hhplus.ecommerce.application.coupon.dto.CouponResult;
import kr.hhplus.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponRepository;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import kr.hhplus.ecommerce.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("[통합테스트] CouponFacade")
@Description("선착순 쿠폰 발급 테스트")
class CouponFacadeIntegrationTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    private Long userId;
    private Long couponId;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("홍길동")
                .build();
        userRepository.save(user);
        this.userId = user.getId();

        Coupon coupon = Coupon.builder()
                .discountPrice(1000L)
                .quantity(100)
                .build();
        couponRepository.save(coupon);
        this.couponId = coupon.getId();
    }

    @Test
    @DisplayName("선착순 쿠폰 발급 성공")
    void firstComeFirstIssue_success() {
        // given
        CouponCriteria.Issue criteria = new CouponCriteria.Issue(userId, couponId);

        // when
        CouponResult.Issued result = couponFacade.couponFirstIssue(criteria);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.couponId()).isEqualTo(couponId);
        assertThat(result.status()).isNotNull();
        assertThat(result.expiredAt()).isNotNull();

        IssuedCoupon saved = issuedCouponRepository.findById(result.id()).orElseThrow();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getCouponId()).isEqualTo(couponId);
    }
}
