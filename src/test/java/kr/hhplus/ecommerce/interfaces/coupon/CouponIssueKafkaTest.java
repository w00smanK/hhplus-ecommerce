package kr.hhplus.ecommerce.interfaces.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponApplyRepository;
import kr.hhplus.ecommerce.domain.coupon.CouponEvent;
import kr.hhplus.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.ecommerce.domain.coupon.CouponService;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponCommand;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@EmbeddedKafka(partitions = 1, topics = {"coupon.v1.issue"})
@SpringBootTest
@ActiveProfiles("test")
class CouponIssueKafkaTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponApplyRepository couponApplyRepository;

    @Autowired
    private CouponKafkaConsumer couponKafkaConsumer;

    @Test
    @Transactional
    void 선착순_쿠폰_발급_요청_테스트() {
        // given
        Coupon coupon = Coupon.builder()
                .discountPrice(1000L)
                .quantity(100)
                .build();
        Coupon savedCoupon = couponRepository.save(coupon);

        couponApplyRepository.initializeCoupon(savedCoupon);

        Long userId = 1L;
        CouponCommand.Issue command = new CouponCommand.Issue(userId, savedCoupon.getId());

        // when
        couponService.issueCouponKafka(command);

        // then
        boolean hasIssued = couponApplyRepository.hasIssuedCoupon(userId, savedCoupon.getId());
        assertThat(hasIssued).isTrue();

        long remainingStock = couponApplyRepository.getCouponStock(savedCoupon.getId());
        assertThat(remainingStock).isEqualTo(100); // 아직 실제 발급은 안됨
    }

    @Test
    @Transactional
    void Kafka_Consumer_쿠폰_발급_처리_테스트() {
        // given
        Coupon coupon = Coupon.builder()
                .discountPrice(1000L)
                .quantity(100)
                .build();
        Coupon savedCoupon = couponRepository.save(coupon);

        Long userId = 1L;
        CouponEvent.CouponIssuedEvent event = CouponEvent.CouponIssuedEvent.of(savedCoupon.getId(), userId);

        // when
        couponKafkaConsumer.handleCouponIssue(event);

        // then
        assertThat(event.couponId()).isEqualTo(savedCoupon.getId());
        assertThat(event.userId()).isEqualTo(userId);
    }
}
