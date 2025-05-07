package kr.hhplus.ecommerce.distributed.point;

import kr.hhplus.ecommerce.domain.point.PointHistoryRepository;
import kr.hhplus.ecommerce.domain.point.PointRepository;
import kr.hhplus.ecommerce.domain.point.PointService;
import kr.hhplus.ecommerce.domain.point.dto.PointCommand;
import kr.hhplus.ecommerce.domain.point.entity.Point;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import kr.hhplus.ecommerce.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("PointService 통합테스트")
class PointServiceIntegrationTest {

    User user;
    Point account;
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private PointHistoryRepository pointHistoryRepository;
    @Autowired
    private PointService pointService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User( "김우경"));
        account = pointRepository.save(new Point(user.getId(), 1000L));
    }


    @Nested
    @DisplayName("차감")
    class Reduce {

        @Test
        @DisplayName("성공")
        void reduce() throws Exception {
            PointCommand.Reduce command = new PointCommand.Reduce(user.getId(), 1000L, null);
            Point result = pointService.reduce(command);

            assertThat(result.getAccount()).isEqualTo(0L);
            assertThat(pointRepository.findByUserId(user.getId()).get().getAccount()).isEqualTo(0L);
            assertThat(pointHistoryRepository.findByUserId(account.getId())).hasSize(1);
        }

        @Test
        @DisplayName("실패 - 잔고 부족")
        void insufficient() {
            PointCommand.Reduce command = new PointCommand.Reduce(user.getId(), 2000L, null);
            Exception ex = assertThrows(Exception.class, () -> pointService.reduce(command));
            assertThat(ex.getMessage()).isEqualTo("잔액이 부족합니다.");
        }
        @Test
        @DisplayName("실패 - 사용자 없음")
        void userNotFound() {
            PointCommand.Reduce command = new PointCommand.Reduce(999L, 1000L, null);
            Exception ex = assertThrows(Exception.class, () -> pointService.reduce(command));
            assertThat(ex.getMessage()).isEqualTo("잔액이 부족합니다.");
        }
    }
}
