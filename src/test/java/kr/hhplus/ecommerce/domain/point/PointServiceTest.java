package kr.hhplus.ecommerce.domain.point;


import kr.hhplus.ecommerce.domain.point.dto.PointCommand;
import kr.hhplus.ecommerce.domain.point.entity.Point;
import kr.hhplus.ecommerce.domain.user.entity.User;
import kr.hhplus.ecommerce.support.MockTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PointServiceTest extends MockTestSupport {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private PointRepository pointRepository;

    @BeforeEach
    void setUp() {
        pointService = new PointService(pointRepository, pointHistoryRepository); // 💥 주입
    }

    @DisplayName("잔액 충전 시, 잔액이 이미 있다면 기존 잔액에 충전한다.")
    @Test
    void charge() {
        // given
        User user = User.builder()
                .id(1L)
                .name("김우경")
                .build();

        long remainAmount = 10_000L;

        Point existPoint = Point.builder()
                .id(1L)
                .userId(user.getId())
                .account(remainAmount)
                .build();

        when(pointRepository.findBy(anyLong())).thenReturn(Optional.ofNullable(existPoint));

        long chargeAmount = 10_000L;
        PointCommand.Charge command = PointCommand.Charge.of(user.getId(), chargeAmount);

        // when
        Point result = pointService.charge(command);

        // then
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getAccount()).isEqualTo(20_000L);
        verify(pointRepository, never()).save(any()); // 이미 존재하는 포인트이므로 저   장 안 함
    }

    @DisplayName("충전 금액이 최대 허용 금액을 넘으면 예외가 발생한다.")
    @ParameterizedTest(name = "충전 금액: {0}원")
    @ValueSource(longs = {Point.MAX_CHARGE_AMOUNT + 1L, Point.MAX_AMOUNT + 1L})
    void chargeExceedingMaxAmount(long invalidAmount) {
        // given
        User user = User.builder()
                .id(1L)
                .name("김우경")
                .build();

        long remainAmount = 1_000_000L;

        Point existPoint = Point.builder()
                .id(1L)
                .userId(user.getId())
                .account(remainAmount)
                .build();

        when(pointRepository.findBy(anyLong())).thenReturn(Optional.ofNullable(existPoint));

        PointCommand.Charge command = PointCommand.Charge.of(user.getId(), invalidAmount);

        // when & then
        assertThatThrownBy(() -> pointService.charge(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 금액을 초과할 수 없습니다.");
    }

    @DisplayName("사용 금액이 잔액보다 많으면 예외가 발생한다.")
    @ParameterizedTest(name = "사용 금액: {0}원")
    @ValueSource(longs = {10_001L, 20_000L, Long.MAX_VALUE})
    void usePoint(long useAmount) {
        // given
        User user = User.builder()
                .id(1L)
                .name("김우경")
                .build();

        long remainAmount = 10_000L;

        Point existPoint = Point.builder()
                .id(1L)
                .userId(user.getId())
                .account(remainAmount)
                .build();

        when(pointRepository.findBy(anyLong())).thenReturn(Optional.ofNullable(existPoint));

        PointCommand.Use command = PointCommand.Use.of(user.getId(), useAmount);

        // when & then
        assertThatThrownBy(() -> pointService.use(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔액이 부족합니다.");
    }
}