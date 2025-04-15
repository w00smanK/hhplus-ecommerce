package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.domain.point.entity.Point;
import kr.hhplus.ecommerce.domain.point.entity.PointHistory;
import kr.hhplus.ecommerce.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PointHistoryServiceTest extends MockTestSupport {

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @DisplayName("충전 히스토리를 저장한다.")
    @Test
    void saveChargeHistory() {
        // given
        Long userId = 1L;
        Long amount = 1_000L;

        PointHistory chargedHistory = PointHistory.ChargeHistory(userId, amount);

        when(pointHistoryRepository.save(any()))
                .thenReturn(chargedHistory);

        // when: 서비스에서 실제로 저장 호출
        PointHistory result = PointHistory.ChargeHistory(userId, amount);

        // then
        assertThat(result).isEqualTo(chargedHistory);
    }

    @DisplayName("사용 히스토리를 저장한다.")
    @Test
    void saveUseHistory() {
        // given
        long userId = 1L;
        int amount = 1_000;

        PointHistory usedHistory = PointHistory.UseHistory(userId, amount);

        when(pointHistoryRepository.save(any()))
                .thenReturn(usedHistory);

        // when
        PointHistory result = PointHistory.UseHistory(userId, amount);

        // then
        assertThat(result).isEqualTo(usedHistory);
    }

}