package kr.hhplus.ecommerce.domain.user;

import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.config.exception.CustomException;
import kr.hhplus.ecommerce.domain.user.dto.UserCommand;
import kr.hhplus.ecommerce.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findByUserId(UserCommand.Find command) {
        return userRepository.findById(command.id())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}
