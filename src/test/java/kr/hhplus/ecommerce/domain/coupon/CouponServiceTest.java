package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponCommand;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponInfo;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import kr.hhplus.ecommerce.domain.coupon.entity.CouponStatus;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("쿠폰")
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private IssuedCouponRepository issuedCouponRepository;

    @InjectMocks
    private CouponService couponService;

    private Long USER_ID;
    private Long COUPON_ID;
    private Long ISSUED_COUPON_ID;

    private Coupon COUPON;
    private IssuedCoupon ISSUED_COUPON;
    private CouponCommand.Use COMMAND;

    @BeforeEach
    void setUp() {
        USER_ID = 1L;
        COUPON_ID = 11L;
        ISSUED_COUPON_ID = 111L;

        COUPON = Coupon.builder()
                .id(COUPON_ID)
                .discountPrice(1000L)
                .quantity(100)
                .build();

        ISSUED_COUPON = IssuedCoupon.builder()
                .id(ISSUED_COUPON_ID)
                .userId(USER_ID)
                .couponId(COUPON_ID)
                .status(CouponStatus.ISSUED)
                .expiredAt(LocalDateTime.now().plusDays(30))
                .build();

        COMMAND = new CouponCommand.Use(USER_ID, COUPON_ID);
    }

    @Nested
    @DisplayName("1. 쿠폰 적용")
    class useCoupon {

        @Test
        @DisplayName("1-1. [성공] 쿠폰 사용 시 상태가 ISSUED → USED 로 변경된다")
        void useCoupon_ok() {
            when(couponRepository.findById(COUPON_ID)).thenReturn(Optional.of(COUPON));
            when(issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID)).thenReturn(Optional.of(ISSUED_COUPON));

            CouponInfo.CouponStock use = couponService.use(COMMAND);

            verify(couponRepository, times(1)).findById(COUPON_ID);
            verify(issuedCouponRepository, times(1)).findByUserIdAndCouponId(USER_ID, COUPON_ID);

            assertThat(use.status()).isEqualTo(CouponStatus.USED);
            assertThat(use.usedAt()).isNotNull();
            assertThat(use.couponId()).isEqualTo(COUPON_ID);
        }

        @Test
        @DisplayName("1-2. [실패] 존재하지 않는 쿠폰 사용 시 예외(NOT_FOUND)")
        void useCoupon_coupon_NotFound() {
            when(couponRepository.findById(COUPON_ID)).thenReturn(Optional.empty());

            Exception exception = assertThrows(Exception.class, () -> couponService.use(COMMAND));

            verify(couponRepository, times(1)).findById(COUPON_ID);
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("1-3. [실패] 사용자에게 발급된 쿠폰이 없을 경우 예외(NOT_FOUND)")
        void useCoupon_issuedCoupon_NotFound() {
            when(couponRepository.findById(COUPON_ID)).thenReturn(Optional.of(COUPON));
            when(issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID)).thenReturn(Optional.empty());

            Exception exception = assertThrows(Exception.class, () -> couponService.use(COMMAND));

            verify(couponRepository, times(1)).findById(COUPON_ID);
            verify(issuedCouponRepository, times(1)).findByUserIdAndCouponId(USER_ID, COUPON_ID);
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("1-4. [실패] 쿠폰 상태가 ISSUED가 아닐 경우 예외(BAD_REQUEST)")
        void useCoupon_BadRequest() {
            IssuedCoupon usedIssuedCoupon = IssuedCoupon.builder()
                    .id(ISSUED_COUPON_ID)
                    .userId(USER_ID)
                    .couponId(COUPON_ID)
                    .status(CouponStatus.USED)
                    .expiredAt(LocalDateTime.now().plusDays(30))
                    .build();

            when(couponRepository.findById(COUPON_ID)).thenReturn(Optional.of(COUPON));
            when(issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID)).thenReturn(Optional.of(usedIssuedCoupon));

            Exception exception = assertThrows(Exception.class, () -> couponService.use(COMMAND));

            verify(couponRepository, times(1)).findById(COUPON_ID);
            verify(issuedCouponRepository, times(1)).findByUserIdAndCouponId(USER_ID, COUPON_ID);
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.BAD_REQUEST.getMessage());
        }
    }

    @Nested
    @DisplayName("2. 쿠폰 발급")
    class issue {

        @Test
        @DisplayName("2-1. [성공] 쿠폰 발급 시 수량 1 감소")
        void issue_ok() {
            when(couponRepository.findById(COUPON_ID)).thenReturn(Optional.of(COUPON));

            couponService.issue(new CouponCommand.Issue(USER_ID, COUPON_ID));

            verify(couponRepository, times(1)).findById(COUPON_ID);
            assertThat(COUPON.getId()).isEqualTo(COUPON_ID);
            assertThat(COUPON.getDiscountPrice()).isEqualTo(1000L);
            assertThat(COUPON.getQuantity()).isEqualTo(99);
        }

        @Test
        @DisplayName("2-2. [실패] 존재하지 않는 쿠폰일 경우 예외(NOT_FOUND)")
        void issue_NotFound() {
            when(couponRepository.findById(COUPON_ID)).thenReturn(Optional.empty());

            Exception exception = assertThrows(Exception.class,
                    () -> couponService.issue(new CouponCommand.Issue(USER_ID, COUPON_ID)));

            verify(couponRepository, times(1)).findById(COUPON_ID);
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("2-3. [실패] 쿠폰 수량이 부족할 경우 예외(BAD_REQUEST)")
        void issue_BadRequest() {
            Coupon insufficientCoupon1 = Coupon.builder().id(1L).quantity(0).build();
            Coupon insufficientCoupon2 = Coupon.builder().id(2L).quantity(-1).build();

            when(couponRepository.findById(1L)).thenReturn(Optional.of(insufficientCoupon1));
            when(couponRepository.findById(2L)).thenReturn(Optional.of(insufficientCoupon2));

            Exception exception1 = assertThrows(Exception.class,
                    () -> couponService.issue(new CouponCommand.Issue(USER_ID, 1L)));

            verify(couponRepository, times(1)).findById(1L);
            assertThat(exception1.getMessage()).isEqualTo(ErrorCode.BAD_REQUEST.getMessage());

            Exception exception2 = assertThrows(Exception.class,
                    () -> couponService.issue(new CouponCommand.Issue(USER_ID, 2L)));

            verify(couponRepository, times(1)).findById(2L);
            assertThat(exception2.getMessage()).isEqualTo(ErrorCode.BAD_REQUEST.getMessage());
        }
    }

    @Nested
    @DisplayName("3. 쿠폰 저장")
    class save {

        @Test
        @DisplayName("3-1. [성공] 쿠폰 저장 시 ISSUED 상태로 저장")
        void save_ok() {
            when(issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID)).thenReturn(Optional.empty());

            IssuedCoupon issuedCoupon = new IssuedCoupon(USER_ID, COUPON_ID);
            when(issuedCouponRepository.save(any(IssuedCoupon.class))).thenReturn(issuedCoupon);

            IssuedCoupon actual = couponService.save(new CouponCommand.Save(USER_ID, COUPON_ID, 1000L));

            verify(issuedCouponRepository, times(1)).findByUserIdAndCouponId(USER_ID, COUPON_ID);
            verify(issuedCouponRepository, times(1)).save(any(IssuedCoupon.class));

            assertThat(actual.getUserId()).isEqualTo(USER_ID);
            assertThat(actual.getCouponId()).isEqualTo(COUPON_ID);
            assertThat(actual.getStatus()).isEqualTo(CouponStatus.ISSUED);
            assertThat(actual.getUsedAt()).isNull();
            assertThat(actual.getExpiredAt()).isNotNull();
        }

        @Test
        @DisplayName("3-2. [실패] 이미 발급된 쿠폰일 경우 예외(BAD_REQUEST)")
        void save_BadRequest() {
            IssuedCoupon issuedCoupon = new IssuedCoupon(USER_ID, COUPON_ID);
            when(issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID)).thenReturn(Optional.of(issuedCoupon));

            Exception exception = assertThrows(Exception.class,
                    () -> couponService.save(new CouponCommand.Save(USER_ID, COUPON_ID, 1000L)));

            verify(issuedCouponRepository, times(1)).findByUserIdAndCouponId(USER_ID, COUPON_ID);
            verify(issuedCouponRepository, never()).save(any(IssuedCoupon.class));
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.BAD_REQUEST.getMessage());
        }
    }
}