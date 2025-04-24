package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.config.exception.CustomException;
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
        Point point = pointRepository.findByUserId(command.getUserId())
                // exception if not found
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        point.charge(command.getAmount());

        pointHistoryRepository.save(new PointHistory(point.getId(), command.getAmount(), TransactionType.CHARGE));

        // history 여부
        return point;
    }

    @Transactional(readOnly = true)
    public Point findPoint(PointCommand.Find command) {
        return pointRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

    @Transactional
    public Point use(PointCommand.Use command) {
        Point point = pointRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        point.use(command.getAmount());

        return point;
    }

    @Transactional
    public Point reduce(PointCommand.Reduce command) throws Exception {

        Point point = pointRepository.findByUserId(command.userId())
                .orElseThrow(() -> new Exception("잔액이 부족합니다."));


        pointHistoryRepository.save(new PointHistory(point.getUserId(), command.issuedCouponId(), command.paymentAmount(), TransactionType.USE));

        return point.reduce(command.paymentAmount());
    }
}
