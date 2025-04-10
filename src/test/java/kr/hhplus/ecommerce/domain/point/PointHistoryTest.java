package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.domain.point.entity.PointHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PointHistoryTest {

    @DisplayName("충전 히스토리 생성")
    @Test
    void createChargeHistory() {
        // given
        long userId = 1L;
        int amount = 1_000;

        // when
        PointHistory history = PointHistory.ChargeHistory(userId, amount);

        // then
        assertThat(history.getType()).isEqualTo(PointHistory.Type.CHARGE);
        assertThat(history.getAmount()).isEqualTo(amount);
    }

    @DisplayName("사용 히스토리 생성")
    @Test
    void createUseHistory() {
        // given
        long userId = 1L;
        int amount = 1_000;

        // when
        PointHistory history = PointHistory.UseHistory(userId, amount);

        // then
        assertThat(history.getType()).isEqualTo(PointHistory.Type.USE);
        assertThat(history.getAmount()).isEqualTo(amount);
    }
}