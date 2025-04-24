package kr.hhplus.ecommerce.interfaces.point;

import kr.hhplus.ecommerce.application.point.PointFacade;
import kr.hhplus.ecommerce.application.point.dto.PointCriteria;
import kr.hhplus.ecommerce.application.point.dto.PointResult;
import kr.hhplus.ecommerce.interfaces.common.StatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointController implements PointApi {

    private final PointFacade pointFacade;

    @Override
    public StatusResponse<PointResponse.UserPoint> getUserPoint(Long userId) {
        PointResult.UserPoint result = pointFacade.findPoint(PointCriteria.Find.of(userId));
        return StatusResponse.of(200, "OK", PointResponse.UserPoint.from(result));
    }

    @Override
    public StatusResponse<PointResponse.UserPoint> chargePoint(PointRequest.Charge request) {
        PointResult.UserPoint result = pointFacade.charge(PointCriteria.Charge.of(request.userId(), request.amount()));
        return StatusResponse.of(200, "OK", PointResponse.UserPoint.from(result));
    }
}
