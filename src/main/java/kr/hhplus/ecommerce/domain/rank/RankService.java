package kr.hhplus.ecommerce.domain.rank;

import kr.hhplus.ecommerce.application.product.dto.ProductCriteria;
import kr.hhplus.ecommerce.application.product.dto.ProductResult;
import kr.hhplus.ecommerce.config.CacheType;
import kr.hhplus.ecommerce.domain.rank.dto.RankCommand;
import kr.hhplus.ecommerce.domain.rank.dto.RankInfo;
import kr.hhplus.ecommerce.domain.rank.entity.Rank;
import kr.hhplus.ecommerce.infra.rank.RankRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 인기 상품 랭킹 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RankService {

    private final RankRepository rankRepository;
    private final RankRedisRepository rankRedisRepository;

    /**
     * 판매 랭킹 생성
     * @param command 랭킹 생성 명령
     */
    @Transactional
    public void createSellRank(RankCommand.CreateList command) {
        List<Rank> ranks = command.commands().stream()
                .map(cmd -> {
                    // Redis에 랭킹 정보 저장
                    rankRedisRepository.addDailyRank(cmd.productId(), Long.valueOf(cmd.quantity()), cmd.date());
                    
                    // DB에 랭킹 정보 저장
                    return Rank.create(cmd.productId(), Long.valueOf(cmd.quantity()), cmd.date());
                })
                .collect(Collectors.toList());

        rankRepository.saveAll(ranks);
        log.info("판매 랭킹 생성 완료 - 날짜: {}, 상품 수: {}", 
                command.commands().get(0).date(), 
                command.commands().size());
    }

    /**
     * 인기 상품 랭킹 조회
     * @param command 랭킹 조회 명령
     * @return 인기 상품 목록
     */
    @Transactional(readOnly = true)
    public RankInfo getRankProducts(RankCommand.RankQuery command) {
        LocalDate date = command.date();
        
        // Redis에서 랭킹 정보 조회
        List<Long> productIds = rankRedisRepository.getTopProductsByDays(
                date, 1, command.top());
        
        // Redis에 데이터가 없으면 DB에서 조회
        if (productIds.isEmpty()) {
            log.info("Redis에 랭킹 정보가 없어 DB에서 조회합니다. 날짜: {}", date);
            List<Rank> ranks = rankRepository.findTopByRankDate(date, command.top());
            
            productIds = ranks.stream()
                    .map(Rank::getProductId)
                    .collect(Collectors.toList());
        }
        
        log.info("인기 상품 랭킹 조회 완료 - 날짜: {}, 상품 수: {}", 
                date, productIds.size());
        
        return RankInfo.of(productIds);
    }

}