package kr.hhplus.ecommerce.infra.user;

import kr.hhplus.ecommerce.domain.user.entity.User;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryImpl implements UserRepository {

    @Override
    public Optional<User> findById(Long memberId) {
        return Optional.empty();
    }
}
