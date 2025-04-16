package kr.hhplus.ecommerce.application.point;

import kr.hhplus.ecommerce.application.point.dto.PointCriteria;
import kr.hhplus.ecommerce.application.point.dto.PointResult;
import kr.hhplus.ecommerce.domain.point.PointService;
import kr.hhplus.ecommerce.domain.point.dto.PointCommand;
import kr.hhplus.ecommerce.domain.point.entity.Point;
import kr.hhplus.ecommerce.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointFacade {

    private final UserService userService;
    private final PointService pointService;

    @Transactional
    public PointResult.UserPoint charge(PointCriteria.Charge criteria) {

        userService.getUser(criteria.getUserId());
        PointCommand.Charge command = criteria.toCommand();
        Point point = pointService.charge(command);

        return PointResult.UserPoint.from(point);
    }

}
