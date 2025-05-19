package kr.hhplus.ecommerce.application.rank;

import kr.hhplus.ecommerce.application.rank.dto.RankCriteria;
import kr.hhplus.ecommerce.application.rank.dto.RankResult;
import kr.hhplus.ecommerce.config.CacheType;
import kr.hhplus.ecommerce.config.RedisCacheTemplate;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.rank.RankService;
import kr.hhplus.ecommerce.domain.rank.dto.RankCommand;
import kr.hhplus.ecommerce.domain.rank.dto.RankInfo;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RankFacade {

    private final ProductService productService;
    private final OrderService orderService;
    private final RankService rankService;
    private final RedisCacheTemplate redisCacheTemplate;

    @Transactional
    public void createDailyRankAt(LocalDate date) {
        OrderCommand.DateQuery orderCommand = OrderCommand.DateQuery.of(date);
        OrderInfo.PaidProducts paidProducts = orderService.getPaidProducts(orderCommand);

        RankCommand.CreateList rankCommand = createListCommand(paidProducts, date);
        rankService.createSellRank(rankCommand);
    }

    @Transactional(readOnly = true)
    public RankResult.PopularProducts getPopularProducts(RankCriteria.PopularProducts criteria) {
        String cacheKey = "top:" + criteria.getTop() + ":days:" + criteria.getDays();

        // 캐시에서 조회
        Optional<RankResult.PopularProducts> cached = redisCacheTemplate.get(
                CacheType.CacheName.POPULAR_PRODUCT, 
                cacheKey, 
                RankResult.PopularProducts.class);

        // 캐시에 있으면 반환
        if (cached.isPresent()) {
            return cached.get();
        }

        // 캐시에 없으면 계산하고 캐시에 저장
        RankResult.PopularProducts result = calculatePopularProducts(criteria.getTop(), criteria.getDays());
        redisCacheTemplate.put(CacheType.CacheName.POPULAR_PRODUCT, cacheKey, result);
        return result;
    }

    @Transactional(readOnly = true)
    public RankResult.PopularProducts updatePopularProducts(RankCriteria.PopularProducts criteria) {
        String cacheKey = "top:" + criteria.getTop() + ":days:" + criteria.getDays();

        // 계산하고 캐시 갱신
        RankResult.PopularProducts result = calculatePopularProducts(criteria.getTop(), criteria.getDays());
        redisCacheTemplate.put(CacheType.CacheName.POPULAR_PRODUCT, cacheKey, result);
        return result;
    }

    private RankCommand.CreateList createListCommand(OrderInfo.PaidProducts paidProducts, LocalDate yesterday) {
        List<RankCommand.Create> commands = paidProducts.getProducts().stream()
            .map(product -> createCommand(product, yesterday))
            .toList();

        return RankCommand.CreateList.of(commands);
    }

    private RankCommand.Create createCommand(OrderInfo.PaidProduct product, LocalDate yesterday) {
        return RankCommand.Create.of(
            product.productId(),
            product.quantity(),
            yesterday
        );
    }

    private RankResult.PopularProducts calculatePopularProducts(int top, int days) {
        LocalDate now = LocalDate.now();

        RankCommand.PopularSellRank popularSellRankCommand = RankCommand.PopularSellRank.of(top, days, now);
        RankInfo.PopularProducts popularProducts = rankService.getPopularSellRank(popularSellRankCommand);

        ProductCommand.Products productsCommand = ProductCommand.Products.of(popularProducts.getProductIds());
        ProductInfo.Products products = productService.getProducts(productsCommand);

        return RankResult.PopularProducts.of(products.getProducts().stream()
            .map(this::toPopularProduct)
            .toList());
    }

    private RankResult.PopularProduct toPopularProduct(ProductInfo.Product product) {
        return RankResult.PopularProduct.builder()
            .productId(product.getProductId())
            .productName(product.getProductName())
            .productPrice(product.getProductPrice())
            .build();
    }
}
