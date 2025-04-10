package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.domain.point.entity.Point;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PointTest {

    private static final long USER_ID = 1L;

    @DisplayName("빈 포인트 객체를 생성한다.")
    @Test
    void createEmptyPoint() {
        // when
        Point point = Point.empty(USER_ID);

        // then
        assertThat(point.getUserId()).isEqualTo(USER_ID);
        assertThat(point.getAccount()).isEqualTo(0L);
    }

    @DisplayName("포인트를 정상적으로 충전한다.")
    @Test
    void chargePoint() {
        // given
        Point point = Point.empty(USER_ID);
        long chargeAmount = 10000L;

        // when
        point.charge(chargeAmount);

        // then
        assertThat(point.getAccount()).isEqualTo(chargeAmount);
    }

    @DisplayName("충전 금액이 최대 허용 금액을 초과하면 예외가 발생한다.")
    @Test
    void chargeExceedsMaxChargeAmount() {
        // given
        Point point = Point.empty(USER_ID);
        long overAmount = Point.MAX_CHARGE_AMOUNT + 1;

        // expect
        assertThatThrownBy(() -> point.charge(overAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 금액을 초과할 수 없습니다.");
    }

    @DisplayName("잔액과 충전 금액의 합이 최대 보유 금액을 초과하면 예외가 발생한다.")
    @Test
    void chargeExceedsMaxBalance() {
        // given
        Point point = new Point(1L, USER_ID, Point.MAX_AMOUNT);
        long extraCharge = 1L;

        // expect
        assertThatThrownBy(() -> point.charge(extraCharge))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 금액을 초과할 수 없습니다.");
    }

    @DisplayName("포인트를 정상적으로 사용할 수 있다.")
    @Test
    void usePoint() {
        // given
        Point point = new Point(1L, USER_ID, 10000L);
        long useAmount = 3000L;

        // when
        point.use(useAmount);

        // then
        assertThat(point.getAccount()).isEqualTo(7000L);
    }

    @DisplayName("잔액보다 많은 포인트를 사용하면 예외가 발생한다.")
    @Test
    void useMoreThanBalanceThrows() {
        // given
        Point point = new Point(1L, USER_ID, 1_000L);
        long useAmount = 2_000L;

        // expect
        assertThatThrownBy(() -> point.use(useAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔액이 부족합니다.");
    }

    @DisplayName("ID가 존재하면 신규 포인트가 아니다.")
    @Test
    void hasId_isNotNew() {
        // given
        Point point = new Point(1L, USER_ID, 0L);

        // then
        assertThat(point.isNew()).isFalse();
    }
}
