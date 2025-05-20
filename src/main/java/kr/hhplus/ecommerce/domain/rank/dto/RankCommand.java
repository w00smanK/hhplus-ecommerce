package kr.hhplus.ecommerce.domain.rank.dto;

import java.time.LocalDate;
import java.util.List;

public class RankCommand {

    public record Create(
            Long productId,
            Integer quantity,
            LocalDate date
    ) {
        public static Create of(Long productId, Integer quantity, LocalDate date) {
            return new Create(productId, quantity, date);
        }
    }

    public record CreateList(
            List<Create> commands
    ) {
        public static CreateList of(List<Create> commands) {
            return new CreateList(commands);
        }
    }

    public record RankQuery(
            int top,
            int days,
            LocalDate date
    ) {
        public static RankQuery of(int top, int days, LocalDate date) {
            return new RankQuery(top, days, date);
        }

        public LocalDate startDate() {
            return date.minusDays(days);
        }

        public LocalDate endDate() {
            return date;
        }
    }
}