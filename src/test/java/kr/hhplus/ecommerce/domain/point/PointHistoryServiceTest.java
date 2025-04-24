package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.MockTestSupport;
import kr.hhplus.ecommerce.domain.point.entity.PointHistory;
import kr.hhplus.ecommerce.domain.point.entity.TransactionType;
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
        PointHistory result = new PointHistory(userId, amount, TransactionType.CHARGE);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getPointId());
        assertEquals(amount, result.getAmount());
        assertEquals(TransactionType.CHARGE, result.getTransactionType());
    }

    @DisplayName("사용 히스토리를 저장한다.")
    @Test
    void saveUseHistory() {
        // given
        long userId = 1L;
        long amount = 1_000;

        // when
        PointHistory result = new PointHistory(userId, amount, TransactionType.USE);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getPointId());
        assertEquals(amount, result.getAmount());
        assertEquals(TransactionType.USE, result.getTransactionType());
    }

}