package kr.hhplus.ecommerce.application.rank;

import kr.hhplus.ecommerce.application.rank.dto.RankCriteria;
import kr.hhplus.ecommerce.application.rank.dto.RankResult;
import kr.hhplus.ecommerce.config.redisConfig.RedisCacheTemplate;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import kr.hhplus.ecommerce.domain.rank.RankService;
import kr.hhplus.ecommerce.domain.rank.dto.RankCommand;
import kr.hhplus.ecommerce.domain.rank.dto.RankInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankFacade {

    private final ProductService productService;
    private final OrderService orderService;
    private final RankService rankService;
    private final RedisCacheTemplate redisCacheTemplate;

    /**
     * 특정 날짜에 대한 일별 판매 순위 생성
     */
    @Transactional
    public void createDailyRankAt(LocalDate date) {
        log.info("일별 판매 순위 생성 시작 - 날짜: {}", date);
        
        try {
            OrderCommand.DateQuery orderCommand = OrderCommand.DateQuery.of(date);
            OrderInfo.PaidProducts paidProducts = orderService.getPaidProducts(orderCommand);

            if (paidProducts.products().isEmpty()) {
                log.info("해당 날짜의 결제 완료 상품이 없습니다 - 날짜: {}", date);
                return;
            }

            // 판매 데이터를 랭킹 명령으로 변환
            RankCommand.CreateList rankCommand = createListCommand(paidProducts, date);
            
            // Redis와 DB에 랭킹 데이터 저장
            rankService.createSellRank(rankCommand);
            log.info("일별 판매 순위 생성 완료 - 날짜: {}, 상품 수: {}", 
                    date, paidProducts.products().size());
        } catch (Exception e) {
            log.error("일별 판매 순위 생성 실패 - 날짜: {}, 오류: {}", date, e.getMessage(), e);
        }
    }


    /**
     * 상품 랭킹 목록 조회
     */
    @Transactional(readOnly = true)
    public RankResult getRankProducts(RankCriteria criteria) {
        log.info("상품 랭킹 목록 조회 - TOP: {}, 기간: {}일", criteria.top(), criteria.days());
        
        try {
            // Redis에서 인기 상품 ID 목록 조회
            RankCommand.RankQuery command = RankCommand.RankQuery.of(
                    criteria.top(), criteria.days(), LocalDate.now());
            RankInfo rankProducts = rankService.getRankProducts(command);
            
            if (rankProducts.productIds().isEmpty()) {
                log.info("랭킹 상품이 없습니다");
                return RankResult.empty();
            }
            
            // 상품 상세 정보 조회
            ProductCommand.Products productsCommand = ProductCommand.Products.of(
                    rankProducts.productIds());
            ProductInfo.RankProducts productRanks = productService.rankProducts(productsCommand);
            
            if (productRanks.getProducts().isEmpty()) {
                log.warn("랭킹 상품 ID에 해당하는 상품 정보가 없습니다 - 상품 ID: {}", 
                        rankProducts.productIds());
                return RankResult.empty();
            }
            
            // 응답 변환 (랭킹 순서 유지)
            List<RankResult.RankProduct> result = new ArrayList<>();
            for (Long productId : rankProducts.productIds()) {
                productRanks.getProducts().stream()
                    .filter(p -> p.getProductId().equals(productId))
                    .findFirst()
                    .ifPresent(product -> result.add(toRankProduct(product)));
            }
            
            log.info("상품 랭킹 목록 조회 완료 - 상품 수: {}", result.size());
            return RankResult.of(result);
        } catch (Exception e) {
            log.error("상품 랭킹 목록 조회 실패 - 오류: {}", e.getMessage(), e);
            return RankResult.empty();
        }
    }

    /**
     * 주문 정보를 랭킹 명령으로 변환
     */
    private RankCommand.CreateList createListCommand(OrderInfo.PaidProducts paidProducts, LocalDate date) {
        List<RankCommand.Create> commands = paidProducts.products().stream()
                .map(product -> createCommand(product, date))
                .collect(Collectors.toList());

        return RankCommand.CreateList.of(commands);
    }

    /**
     * 개별 상품 주문 정보를 랭킹 명령으로 변환
     */
    private RankCommand.Create createCommand(OrderInfo.PaidProduct product, LocalDate date) {
        return RankCommand.Create.of(
                product.productId(),
                product.quantity().intValue(),
                date
        );
    }

    /**
     * 상품 정보를 랭킹 상품 응답으로 변환
     */
    private RankResult.RankProduct toRankProduct(ProductInfo.RankProduct product) {
        return new RankResult.RankProduct(
                product.getProductId(),
                product.getProductName()
        );
    }
}