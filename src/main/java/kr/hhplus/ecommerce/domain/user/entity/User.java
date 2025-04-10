package kr.hhplus.ecommerce.domain.user.entity;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "user")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class User  extends BaseEntity {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Builder
    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
