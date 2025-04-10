package kr.hhplus.ecommerce.domain.user;

import kr.hhplus.ecommerce.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    Optional<User> findById(Long userId);
}
