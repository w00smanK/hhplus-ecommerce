package kr.hhplus.ecommerce.application.point;

import kr.hhplus.ecommerce.application.point.dto.PointCriteria;
import kr.hhplus.ecommerce.application.point.dto.PointResult;
import kr.hhplus.ecommerce.domain.point.PointRepository;
import kr.hhplus.ecommerce.domain.point.entity.Point;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import kr.hhplus.ecommerce.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("[통합테스트] PointFacade")
@Description("로그인되어있다는 가정 / 음수충전못함")
class PointFacadeTest {

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        // 유저 및 초기 포인트 세팅
        User user = User.builder()
                .name("김우경")
                .build();

        userRepository.save(user);

        Point point = Point.builder()
                .userId(user.getId())
                .account(50_000L)
                .build();

        pointRepository.save(point);
        this.userId = user.getId();
    }

    @Test
    @DisplayName("포인트 충전이 성공적으로 이루어지는지 확인")
    void charge_success() {
        // given
        long chargeAmount = 10_000L;
        PointCriteria.Charge criteria = PointCriteria.Charge.of(userId, chargeAmount);

        // when
        PointResult.UserPoint result = pointFacade.charge(criteria);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.account()).isEqualTo(60_000L); // 기존 50,000 + 10,000
    }

    @Test
    @DisplayName("포인트 조회 성공")
    void getPoint_success() {
        // given
        Long userId = this.userId;
        Point point = pointRepository.findByUserId(userId).orElseThrow();
        PointCriteria.Find criteria = PointCriteria.Find.of(userId);

        // when
        PointResult.UserPoint result = pointFacade.findPoint(criteria);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.account()).isEqualTo(point.getAccount());
    }

}

