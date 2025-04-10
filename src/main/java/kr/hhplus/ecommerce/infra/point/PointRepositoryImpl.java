package kr.hhplus.ecommerce.infra.point;


import kr.hhplus.ecommerce.domain.point.entity.Point;
import kr.hhplus.ecommerce.domain.point.PointRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PointRepositoryImpl implements PointRepository {

    @Override
    public Optional <Point> findBy(Long userId) {
        return  Optional.empty();
    }

    @Override
    public Point save(Point Point) {
        return null;
    }
}

