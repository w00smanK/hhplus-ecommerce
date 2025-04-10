package kr.hhplus.ecommerce.application.point;

import kr.hhplus.ecommerce.application.point.dto.PointCriteria;
import kr.hhplus.ecommerce.application.point.dto.PointResult;
import kr.hhplus.ecommerce.domain.point.PointHistoryService;
import kr.hhplus.ecommerce.domain.point.PointService;
import kr.hhplus.ecommerce.domain.point.dto.PointCommand;
import kr.hhplus.ecommerce.domain.point.dto.PointInfo;
import kr.hhplus.ecommerce.domain.point.entity.Point;
import kr.hhplus.ecommerce.domain.user.UserService;
import kr.hhplus.ecommerce.domain.user.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointFacade {

    private final UserService userService;
    private final PointService pointService;
    private final PointHistoryService pointHistoryService;


    public PointResult.Point charge(PointCriteria.Charge criteria) {

        userService.getUser(criteria.getUserId());
        PointCommand.Charge command = criteria.toCommand();
        Point point = pointService.charge(command);

        pointHistoryService.record(command);

        return PointResult.Point.of(point.getAccount());
    }

}
