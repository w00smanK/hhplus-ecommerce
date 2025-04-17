    package kr.hhplus.ecommerce.infra.user;

    import kr.hhplus.ecommerce.domain.user.entity.User;
    import kr.hhplus.ecommerce.domain.user.UserRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;

    import java.util.Optional;

    @Component
    @RequiredArgsConstructor
    public class UserRepositoryImpl implements UserRepository {

        private final UserJpaRepository userJpaRepository;

        @Override
        public Optional<User> findById(Long userId) {
            return userJpaRepository.findById(userId);
        }

        @Override
        public User save(User user) {
            return userJpaRepository.save(user);
        }
    }
