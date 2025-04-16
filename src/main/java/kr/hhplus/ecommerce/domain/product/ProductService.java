package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import kr.hhplus.ecommerce.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductInfo.ProductAggregate findProduct(ProductCommand.findById command) {

        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        List<ProductOption> productOptions = productOptionRepository.findByProductId(command.productId());

        return ProductInfo.ProductAggregate.from(product, productOptions);
    }


    public ProductInfo.ProductDetail getProduct(Long productId) {
            Product product = productRepository.findById(productId);
//                .orElseThrow(() -> new IllegalArgumentException("포인트 정보가 없습니다."));

        if (product == null) {
            throw new IllegalArgumentException("상품이 존재하지 않습니다.");
        }

        return ProductInfo.ProductDetail.from(product);
    }
}
