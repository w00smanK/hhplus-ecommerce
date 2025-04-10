package kr.hhplus.ecommerce.domain.point.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointInfo {

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class Point {

        private final long amount;
    }
}
