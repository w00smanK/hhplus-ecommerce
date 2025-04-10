package kr.hhplus.ecommerce.domain.user;

import kr.hhplus.ecommerce.domain.user.dto.UserInfo;
import kr.hhplus.ecommerce.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfo.User getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        return UserInfo.User.builder()
                .userId(user.getId())
                .username(user.getName())
                .build();
    }
}
