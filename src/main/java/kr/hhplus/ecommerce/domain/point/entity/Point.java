package kr.hhplus.ecommerce.domain.point.entity;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "user_point")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Point extends BaseEntity {

    public static final int MAX_CHARGE_AMOUNT = 1000000;
    public static final long MAX_AMOUNT = 2000000;


    @Id
    @Column(name = "point_id")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long account;

    @Builder
    public Point(Long id, Long userId, Long account) {
        this.id = id;
        this.userId = userId;
        this.account = account;
    }

    public static Point empty(Long userId) {
        return new Point(null, userId, 0L);
    }

    public void charge(Long account) {
        if (account > MAX_CHARGE_AMOUNT) {
            throw new IllegalArgumentException("최대 금액을 초과할 수 없습니다.");
        }

        if (this.account + account > MAX_AMOUNT) {
            throw new IllegalArgumentException("최대 금액을 초과할 수 없습니다.");
        }

        this.account += account;
    }

    public void use(Long account) {

        if (this.account < account) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        this.account -= account;
    }

    public boolean isNew() {
        return this.id == null;
    }
}
