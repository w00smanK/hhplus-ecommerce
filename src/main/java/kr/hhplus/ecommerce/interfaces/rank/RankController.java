package kr.hhplus.ecommerce.interfaces.rank;

import kr.hhplus.ecommerce.application.rank.RankFacade;
import kr.hhplus.ecommerce.application.rank.dto.RankCriteria;
import kr.hhplus.ecommerce.application.rank.dto.RankResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/rank")
@RequiredArgsConstructor
public class RankController {

    private final RankFacade rankFacade;

    /**
     * 상품 랭킹 TOP 5 목록 조회
     * @return 상품 랭킹 TOP 5 목록
     */
    @GetMapping("/products")
    public ResponseEntity<RankResult> getRankProducts(
            @RequestParam(required = false, defaultValue = "5") int top,
            @RequestParam(required = false, defaultValue = "3") int days) {
        
        log.info("상품 랭킹 목록 요청 - TOP: {}, 기간: {} 일", top, days);
        RankCriteria criteria = RankCriteria.of(top, days);
        RankResult result = rankFacade.getRankProducts(criteria);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/daily")
    public ResponseEntity<Map<String, String>> createDailyRank(
            @RequestParam(required = false) String date) {
        
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now().minusDays(1);
        log.info("일별 판매 순위 수동 생성 요청 - 날짜: {}", targetDate);
        
        rankFacade.createDailyRankAt(targetDate);
        return ResponseEntity.ok(Map.of("status", "success", "message", "일별 판매 순위 생성 완료"));
    }
}