package kr.hhplus.ecommerce.domain.rank.dto;

import java.util.Collections;
import java.util.List;

public record RankInfo(List<Long> productIds) {
    
    public static RankInfo of(List<Long> productIds) {
        return new RankInfo(productIds);
    }
    
    public static RankInfo empty() {
        return new RankInfo(Collections.emptyList());
    }
}
