package kr.hhplus.ecommerce.domain.user;

import kr.hhplus.ecommerce.domain.user.dto.UserInfo;
import kr.hhplus.ecommerce.domain.user.entity.User;
import kr.hhplus.ecommerce.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class UserServiceTest extends MockTestSupport {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @DisplayName("사용자 ID로 조회 시, 사용자가 존재하지 않으면 예외가 발생한다.")
    @Test
    void getNotExistUser() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUser(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자가 존재하지 않습니다.");
    }

    @DisplayName("사용자 ID로 사용자를 조회한다.")
    @Test
    void getUser() {
        // given
        User user = User.builder()
                .id(1L)
                .name("김우경")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        // when
        UserInfo.User result = userService.getUser(1L);

        // then
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getUsername()).isEqualTo(user.getName());
    }
}
