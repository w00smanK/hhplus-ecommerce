package kr.hhplus.ecommerce.infra.user;

import kr.hhplus.ecommerce.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
