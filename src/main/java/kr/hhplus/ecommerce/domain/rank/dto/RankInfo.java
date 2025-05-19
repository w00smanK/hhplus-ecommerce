package kr.hhplus.ecommerce.domain.rank.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 인기 상품 랭킹 정보 객체
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankInfo {

    /**
     * 인기 상품 목록 정보
     */
    @Getter
    public static class PopularProducts {
        private final List<Long> productIds;

        private PopularProducts(List<Long> productIds) {
            this.productIds = productIds;
        }

        public static PopularProducts of(List<Long> productIds) {
            return new PopularProducts(productIds);
        }
    }
}