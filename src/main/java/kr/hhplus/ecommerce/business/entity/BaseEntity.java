//package kr.hhplus.ecommerce.business.entity;
//
//
//import jakarta.persistence.Column;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.LastModifiedDate;
//
//import java.time.LocalDateTime;
//
///**
// * 추후 사용될 수 있는 BaseEntity 클래스
// * BaseEntity
// * <p>
// * BaseEntity는 모든 엔티티의 공통 속성을 정의하는 추상 클래스입니다.
// * createdAt과 registered_dt 필드를 포함하고 있으며, 이 필드들은 각각 생성일자와 수정일자를 나타냅니다.
// * </p>
// */
//@Getter
//@Setter
//public abstract class BaseEntity {
//
//    @CreatedDate
//    @Column(name = "create_at", nullable = false, updatable = false)
//    protected LocalDateTime createdAt = LocalDateTime.now();
//
//    @LastModifiedDate
//    @Column(name = "updated_at")
//    private LocalDateTime registered_dt;
//
//}