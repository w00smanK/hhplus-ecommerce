package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.domain.point.dto.PointHistoryCommand;
import kr.hhplus.ecommerce.domain.point.entity.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointHistoryService {
    private final PointHistoryRepository pointHistoryRepository;

    public PointHistory record (PointHistoryCommand.Record command) {
        PointHistory history = new PointHistory(
                command.getUserId(),
                command.getAmount(),
                command.getType()
        );
        return pointHistoryRepository.save(history);
    }
}
