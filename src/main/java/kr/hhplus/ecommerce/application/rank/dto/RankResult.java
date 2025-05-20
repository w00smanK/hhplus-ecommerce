package kr.hhplus.ecommerce.application.rank.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankResult {
    
    private List<RankProduct> products;

    private RankResult(List<RankProduct> products) {
        this.products = products;
    }

    public static RankResult of(List<RankProduct> products) {
        return new RankResult(products);
    }

    public static RankResult empty() {
        return new RankResult(Collections.emptyList());
    }

    public List<RankProduct> getProducts() {
        return products;
    }

    public record RankProduct(
        Long productId,
        String productName
    ) {
    }
}
