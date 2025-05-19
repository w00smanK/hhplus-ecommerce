package kr.hhplus.ecommerce.interfaces.scheduler;

import kr.hhplus.ecommerce.application.rank.dto.RankCriteria;
import kr.hhplus.ecommerce.application.rank.RankFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 인기 상품 일일 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PopularProductScheduler {

    private final RankFacade rankFacade;

    @Scheduled(cron = "0 0 0 * * *")
    public void createDailyRank() {
        log.info("===== 일일 인기 상품 랭킹 생성 스케줄러 실행 =====");
        try {
            // 전일 주문 데이터 기반으로 인기 상품 랭킹 생성
            LocalDate yesterday = LocalDate.now().minusDays(1);
            rankFacade.createDailyRankAt(yesterday);
            log.info("===== 일일 인기 상품 랭킹 생성 완료 - 날짜: {} =====", yesterday);
            
            // 인기 상품 캐시 갱신
            updatePopularProductsCache();
        } catch (Exception e) {
            log.error("일일 인기 상품 랭킹 생성 중 오류 발생", e);
        }
    }
    
    /**
     * 기본 설정(TOP 5, 최근 3일)으로 인기 상품 캐시를 갱신
     */
    private void updatePopularProductsCache() {
        log.info("===== 인기 상품 캐시 갱신 시작 =====");
        try {
            rankFacade.updatePopularProducts(RankCriteria.PopularProducts.ofTop5Days3());
            log.info("===== 인기 상품 캐시 갱신 완료 =====");
        } catch (Exception e) {
            log.error("인기 상품 캐시 갱신 중 오류 발생", e);
        }
    }
}