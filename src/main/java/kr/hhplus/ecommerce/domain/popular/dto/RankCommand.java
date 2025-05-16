package kr.hhplus.ecommerce.domain.popular.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 인기 상품 랭킹 명령 객체
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankCommand {

    /**
     * 인기 상품 랭킹 생성 명령
     */
    @Getter
    public static class Create {
        private final Long productId;
        private final Long quantity;
        private final LocalDate date;

        private Create(Long productId, Long quantity, LocalDate date) {
            this.productId = productId;
            this.quantity = quantity;
            this.date = date;
        }

        public static Create of(Long productId, Long quantity, LocalDate date) {
            return new Create(productId, quantity, date);
        }
    }

    /**
     * 인기 상품 랭킹 생성 목록 명령
     */
    @Getter
    public static class CreateList {
        private final List<Create> commands;

        private CreateList(List<Create> commands) {
            this.commands = commands;
        }

        public static CreateList of(List<Create> commands) {
            return new CreateList(commands);
        }
    }

    /**
     * 인기 상품 랭킹 조회 명령
     */
    @Getter
    public static class PopularSellRank {
        private final int top;
        private final int days;
        private final LocalDate date;

        private PopularSellRank(int top, int days, LocalDate date) {
            this.top = top;
            this.days = days;
            this.date = date;
        }

        public static PopularSellRank of(int top, int days, LocalDate date) {
            return new PopularSellRank(top, days, date);
        }
    }
}