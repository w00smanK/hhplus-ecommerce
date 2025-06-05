package kr.hhplus.ecommerce.infra.product;

import kr.hhplus.ecommerce.domain.product.ProductEvent;
import kr.hhplus.ecommerce.domain.product.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisherImpl implements ProductEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(ProductEvent.StockDeducted event) {
        applicationEventPublisher.publishEvent(event);
    }


    @Override
    public void publish(ProductEvent.StockInsufficient event) {
        applicationEventPublisher.publishEvent(event);
    }
}
