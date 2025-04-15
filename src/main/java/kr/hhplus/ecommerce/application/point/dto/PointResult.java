package kr.hhplus.ecommerce.application.point.dto;

import kr.hhplus.ecommerce.domain.point.entity.Point;
import lombok.Builder;

public record PointResult() {

    @Builder
    public record UserPoint(
            Long id,
            Long userId,
            Long account
    ) {
        public static UserPoint from(Point point) {
            return new UserPoint(
                    point.getId(),
                    point.getUserId(),
                    point.getAccount()
            );
        }
    }
}
