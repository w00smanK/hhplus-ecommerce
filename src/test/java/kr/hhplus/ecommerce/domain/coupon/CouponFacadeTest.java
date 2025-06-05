package kr.hhplus.ecommerce.domain.coupon;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CouponFacadeTest {

    @Test
    void 쿠폰이벤트_생성_테스트() {
        // given
        Long couponId = 1L;
        Long userId = 100L;

        // when
        CouponEvent.CouponIssuedEvent event = CouponEvent.CouponIssuedEvent.of(couponId, userId);

        // then
        assertThat(event).isNotNull();
        assertThat(event.couponId()).isEqualTo(couponId);
        assertThat(event.userId()).isEqualTo(userId);
    }

    @Test
    void 쿠폰이벤트_정상_생성() {
        // given & when
        CouponEvent.CouponIssuedEvent event = CouponEvent.CouponIssuedEvent.of(1L, 100L);

        // then
        assertThat(event.couponId()).isEqualTo(1L);
        assertThat(event.userId()).isEqualTo(100L);
    }
}
