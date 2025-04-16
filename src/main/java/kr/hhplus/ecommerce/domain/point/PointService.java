package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.domain.point.dto.PointCommand;
import kr.hhplus.ecommerce.domain.point.entity.Point;
import kr.hhplus.ecommerce.domain.point.entity.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public Point charge(PointCommand.Charge command) {
        Point point = pointRepository.findBy(command.getUserId())
                // exception if not found
                .orElse(Point.empty(command.getUserId()));

        point.charge(command.getAmount());
        PointHistory pointHistory = PointHistory.ChargeHistory(command.getUserId(), command.getAmount());
        pointHistoryRepository.save(pointHistory); // save point history

        // history 여부
        return point;
    }

    public Point use(PointCommand.Use command) {
        Point point = pointRepository.findBy(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보가 없습니다."));
        point.use(command.getAmount());

        return point;
    }
}
