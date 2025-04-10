package kr.hhplus.ecommerce.domain.point.dto;

import kr.hhplus.ecommerce.domain.point.entity.PointHistory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointHistoryCommand {

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class Record {
        private final Long userId;
        private final int amount;
        private final PointHistory.Type type;
    }
}
