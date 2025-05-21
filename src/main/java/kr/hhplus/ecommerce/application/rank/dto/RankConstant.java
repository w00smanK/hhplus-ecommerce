package kr.hhplus.ecommerce.application.rank.dto;

public enum RankConstant {
    TOP_5(5),
    DAYS_1(1);

    private final int value;

    RankConstant(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}