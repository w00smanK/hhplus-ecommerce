package kr.hhplus.ecommerce.application.rank.dto;

public record RankCriteria(
    int top,
    int days
) {
    public static RankCriteria of(int top, int days) {
        return new RankCriteria(top, days);
    }
}
