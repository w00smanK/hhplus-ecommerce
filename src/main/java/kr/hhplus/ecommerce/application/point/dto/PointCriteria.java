package kr.hhplus.ecommerce.application.point.dto;


import kr.hhplus.ecommerce.domain.point.dto.PointCommand;
import kr.hhplus.ecommerce.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointCriteria {

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class Charge {

        private final Long userId;
        private final Long amount;

        public PointCommand.Charge toCommand() {
            return PointCommand.Charge.of(userId, amount);
        }

    }
}
