package kr.hhplus.ecommerce.application.point.dto;


import kr.hhplus.ecommerce.domain.point.dto.PointCommand;
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

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class Find {

        private final Long userId;

        public PointCommand.Find toCommand() {
            return PointCommand.Find.of(userId);
        }
    }
}
