package kr.hhplus.ecommerce.interfaces.point;


import kr.hhplus.ecommerce.application.point.dto.PointResult;

public record PointResponse() {
    public record UserPoint(
            Long id,
            Long userId,
            Long account
    ) {
        public static UserPoint from(PointResult.UserPoint result) {
            return new UserPoint(
                    result.id(),
                    result.userId(),
                    result.account()
            );
        }
    }

}