package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.domain.point.dto.PointHistoryCommand;
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

    @InjectMocks
    private PointHistoryService pointHistoryService;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @DisplayName("충전 히스토리를 저장한다.")
    @Test
    void saveChargeHistory() {
        // given
        long userId = 1L;
        int amount = 1_000;

        PointHistory chargedHistory = PointHistory.ChargeHistory(userId, amount);

        when(pointHistoryRepository.save(any()))
                .thenReturn(chargedHistory);

        PointHistoryCommand.Record command = PointHistoryCommand.Record.of(userId, amount, PointHistory.Type.CHARGE);

        // when
        PointHistory result = pointHistoryService.record(command);

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

        PointHistoryCommand.Record command = PointHistoryCommand.Record.of(userId, amount, PointHistory.Type.USE);

        // when
        PointHistory result = pointHistoryService.record(command);

        // then
        assertThat(result).isEqualTo(usedHistory);
    }

}