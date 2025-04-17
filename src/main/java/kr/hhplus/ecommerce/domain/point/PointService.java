package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.domain.point.dto.PointCommand;
import kr.hhplus.ecommerce.domain.point.entity.Point;
import kr.hhplus.ecommerce.domain.point.entity.PointHistory;
import kr.hhplus.ecommerce.domain.point.entity.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public Point charge(PointCommand.Charge command) {
        Point point = pointRepository.findBy(command.getUserId())
                // exception if not found
                .orElse(Point.empty(command.getUserId()));

        point.charge(command.getAmount());

        pointHistoryRepository.save(new PointHistory(point.getId(), command.getAmount(), TransactionType.CHARGE));

        // history 여부
        return point;
    }

    @Transactional(readOnly = true)
    public Point findPoint(PointCommand.Find command) {
        return pointRepository.findBy(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보가 없습니다."));
    }

    @Transactional
    public Point use(PointCommand.Use command) {
        Point point = pointRepository.findBy(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보가 없습니다."));
        point.use(command.getAmount());

        return point;
    }

    @Transactional
    public Point reduce(PointCommand.Reduce command) throws Exception {

        Point point = pointRepository.findBy(command.userId())
                .orElseThrow(() -> new Exception(ErrorCode.NOT_FOUND.getMessage()));


        pointHistoryRepository.save(new PointHistory(point.getUserId(), command.issuedCouponId(), command.paymentAmount(), TransactionType.USE));

        return point.reduce(command.paymentAmount());
    }
}
