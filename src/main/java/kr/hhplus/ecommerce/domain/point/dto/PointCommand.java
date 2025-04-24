package kr.hhplus.ecommerce.domain.point.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
public class PointCommand {
    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class Charge {
        private final Long userId;
        private final long amount;
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class Use {
        private final Long userId;
        private final long amount;
    }

    public record Reduce(
            Long userId,
            Long paymentAmount,
            Long issuedCouponId
    ) {
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Find {
        private Long userId;

        public static Find of(Long userId) {
            return new Find(userId);
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create {
        private Long userId;
    }
}
