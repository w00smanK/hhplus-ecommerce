package kr.hhplus.ecommerce.interfaces.point;

public record PointRequest() {

    public record Charge(
            long userId,
            long amount
    ) {
    }

}
