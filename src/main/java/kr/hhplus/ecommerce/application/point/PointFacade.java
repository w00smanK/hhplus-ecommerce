    package kr.hhplus.ecommerce.application.point;

    import kr.hhplus.ecommerce.application.point.dto.PointCriteria;
    import kr.hhplus.ecommerce.application.point.dto.PointResult;
    import kr.hhplus.ecommerce.application.user.dto.UserCriteria;
    import kr.hhplus.ecommerce.domain.point.PointService;
    import kr.hhplus.ecommerce.domain.point.dto.PointCommand;
    import kr.hhplus.ecommerce.domain.point.entity.Point;
    import kr.hhplus.ecommerce.domain.user.UserService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;

    @Component
    @RequiredArgsConstructor
    public class PointFacade {

        private final UserService userService;
        private final PointService pointService;

        public PointResult.UserPoint charge(PointCriteria.Charge criteria) {

            userService.findByUserId(UserCriteria.Find.toCommand(criteria.getUserId()));
            PointCommand.Charge command = criteria.toCommand();
            Point point = pointService.charge(command);

            return PointResult.UserPoint.from(point);
        }
        public PointResult.UserPoint findPoint(PointCriteria.Find criteria) {

            userService.findByUserId(UserCriteria.Find.toCommand(criteria.getUserId()));
            return PointResult.UserPoint.from(pointService.findPoint(criteria.toCommand()));
        }

    }
