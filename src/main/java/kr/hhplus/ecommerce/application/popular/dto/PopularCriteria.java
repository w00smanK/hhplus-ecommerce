package kr.hhplus.ecommerce.application.popular.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PopularCriteria {

    @Getter
    public static class PopularProducts {

        private final int top;
        private final int days;

        private PopularProducts(int top, int days) {
            this.top = top;
            this.days = days;
        }

        public static PopularProducts of(int top, int days) {
            return new PopularProducts(top, days);
        }

        public static PopularProducts ofTop5Days3() {
            return new PopularProducts(PopularConstant.TOP_5, PopularConstant.DAYS_3);
        }
    }
}
