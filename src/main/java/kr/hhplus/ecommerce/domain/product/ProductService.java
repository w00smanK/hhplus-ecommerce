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

    public ProductInfo.OrderProducts getOrderProducts(ProductCommand.OrderProducts command) {
        List<ProductInfo.OrderProduct> orderProducts = command.getProducts().stream()
                .map(this::toOrderProductInfo)
                .toList();

        return ProductInfo.OrderProducts.of(orderProducts);
    }


    public ProductInfo.Products getProducts(ProductCommand.Products command) {
        List<ProductInfo.Product> products = productRepository.findByIds(command.getProductIds()).stream()
                .map(this::toProductInfo)
                .toList();

        return ProductInfo.Products.of(products);
    }

    private ProductInfo.OrderProduct toOrderProductInfo(ProductCommand.OrderProduct command) {
        Product product = getProduct(command);

        return ProductInfo.OrderProduct.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .quantity(command.getQuantity())
                .build();
    }

    private ProductInfo.Product toProductInfo(Product product) {
        return ProductInfo.Product.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .build();
    }

    private Product getProduct(ProductCommand.OrderProduct command) {
        Product product = productRepository.findById(command.getProductId());

        if (product == null) {
            throw new IllegalArgumentException("상품이 존재하지 않습니다.");
        }

        return product;
    }
}
