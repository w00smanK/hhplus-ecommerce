package kr.hhplus.ecommerce.domain.point.dto;

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

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class Find {
        private final Long userId;
    }
}
