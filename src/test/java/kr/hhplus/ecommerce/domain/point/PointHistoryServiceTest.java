package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.domain.point.entity.PointHistory;
import kr.hhplus.ecommerce.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PointHistoryServiceTest extends MockTestSupport {


    @DisplayName("충전 히스토리를 저장한다.")
    @Test
    void saveChargeHistory() {
        // given
        Long userId = 1L;
        Long amount = 1_000L;

        // when: 서비스에서 실제로 저장 호출
        PointHistory result = PointHistory.ChargeHistory(userId, amount);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(amount, result.getAmount());
        assertEquals(PointHistory.Type.CHARGE, result.getType());
    }

    @DisplayName("사용 히스토리를 저장한다.")
    @Test
    void saveUseHistory() {
        // given
        long userId = 1L;
        int amount = 1_000;

        // when
        PointHistory result = PointHistory.UseHistory(userId, amount);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(amount, result.getAmount());
        assertEquals(PointHistory.Type.USE, result.getType());
    }

}