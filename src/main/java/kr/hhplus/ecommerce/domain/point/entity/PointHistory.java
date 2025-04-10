package kr.hhplus.ecommerce.domain.point.entity;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PointHistory extends BaseEntity {

    @Id
    @Column(name = "point_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long userId;
    private int amount;

    @Enumerated(EnumType.STRING)
    private Type type;

    public PointHistory(long userId, int amount, Type type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
    }

    public static PointHistory ChargeHistory(long userId, int amount) {
        return new PointHistory(userId, amount, Type.CHARGE);
    }

    public static PointHistory UseHistory(long userId, int amount) {
        return new PointHistory(userId, amount, Type.USE);
    }

    public enum Type {
        CHARGE, USE
    }

}
