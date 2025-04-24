# e-Commerce 시스템 동시성 이슈 리포트

> **작성일**: 2024-04-25  
> **작성자**: 김우경   


## ✅ 배경

**동시성 문제**  
  공유 자원에 대해 여러 트랜잭션이 동시에 접근하여 데이터 정합성에 문제가 생기는 현상.
  - Dirty Read (더티 리드)
  - Non-Repeatable Read (반복 불가능 읽기)
  - Lost Update (업데이트 손실)
---


## ✅ 문제 분석

### 1️⃣ 잔액 충전 및 차감

+ 사용자가 동시에 포인트 충전 또는 차감 요청을 보낼 경우, 최종 잔액이 기대값과 다르게 저장되는 문제가 발생할 수 있다.
+ 대표적인 Lost Update(갱신 손실) 현상으로, 동시성 제어가 적용되지 않으면 빈번히 발생한다.

### 2️⃣ 선착순 쿠폰 발급

+ 동시에 다수의 발급 요청이 발생하면, 잔여 수량 갱신 누락으로 인해 선착순 쿠폰이 예정 수량보다 초과 발급될 수 있다.
  
### 3️⃣ 재고 차감

+ 재고 차감 요청이 동시에 발생하면, 여러 트랜잭션이 동일한 재고 수량을 기준으로 차감을 시도하게 된다.
+ 동일 상품에 대한 동시 주문이 반복되면, 재고가 비정상적으로 차감되지 않는 문제가 발생할 수 있다. 
---
## ✅ 해결 방법
### 1. 잔액 충전 및 차감

| 항목    | 내용                                                              |
|:------|:----------------------------------------------------------------|
| 문제 상황 | 포인트 충전/사용 요청이 동시에 발생할 경우 잔액 불일치                                 |
| 발생 이슈 | Lost Update                                                     |
| 해결 방안 | Optimistic Lock Lock(`@Lock(OPTIMISTIC)`), Lock (`@Version`) 적용 |

---


### 2. 선착순 쿠폰 발급

| 항목       | 내용                                                                |
|:---------|:------------------------------------------------------------------|
| 문제 상황    | 쿠폰 수량 불일치 발생 가능성                                                  |
| 발생 이슈    | Lost Update                                                       |
| 해결 방안    | Optimistic Lock(`@Lock(OPTIMISTIC)`), Lock (`@Version`)을 통한 수량 차감 |
| 향후 개선 사항 | Redis 기반 Queue를 통한 동시성/확장성 보장                                     |
---

### 3. 재고 차감

| 항목    | 내용 |
|:------|:---|
| 문제 상황 | 상품 주문 요청이 동시에 발생해 재고 차감 실패 가능성 |
| 발생 이슈 | Lost Update, 데이터 불일치 |
| 해결 방안 | Pessimistic Lock (`@Lock(PESSIMISTIC_WRITE)`) 적용 |
---

## ✅ 실험 결과
### 1. 잔액 충전 및 차감
 금액(300원)의 사용과 금액(500원)의 충전을 각 50회씩 반복하여 최종 금액은 초기 금액의 +1000원 예상 ->  하지만 동시성 이슈 발생으로
예상과는 다른 잔고액 조회

[AS-IS]
```java

public interface PointJpaRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByUserId(Long userId);

}
```

[TO-BE]
```java

@Version
Long version; --> (@Version 및 컬럼 추가) 적용

@Lock(LockModeType.OPTIMISTIC)
@Query("SELECT p From Point p WHERE p.userId = :userId")
Optional<Point> findByUserIdWithOptimisticLock(@Param("userId")Long userId);

Hibernate:
update
        point
set
  account=?,
  registered_at=?,
  updated_at=?,
  user_id=?,
  version=?
where
    id=?
and version=?
}
```

[RESULT]
```java
int threadCount = 2;
Long userId = 1L;
Point point = Point.create(userId);
        point.charge(1_000L);
        pointRepository.save(point);
```

동시에 충전/사용이 들어오면 락을 통해 하나의 요청만 허용

잔액(Point)의 경우 충돌 상황이 빈번하지 않을 것으로 판단되어 낙관적 락 적용

[hhplus] [           main] k.h.e.c.point.PointConcurrencyTest       : ✅ 최종 잔액: 1500



### 2. 선착순 쿠폰 발급
[AS-IS]

선착순으로 발급 가능한 쿠폰에 대하여 발급 요청이 동시에 다수 발생할 경우 -> 쿠폰 차감이 정상적으로 이루어 지지 않음
```java
public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
```

[TO-BE]

```java 

@Version
Long version; --> (@Version 및 컬럼 추가) 적용


@Lock(LockModeType.OPTIMISTIC)
@Query("SELECT c FROM Coupon c WHERE c.id = :id")
Optional<Coupon> findByIdWithLock(@Param("id") Long id);


```
+ 특정 프로모션에 트래픽이 집중되면 쿠폰 발급 시 대기 시간이 증가 
+ 이벤트 특성상 실패 허용이 가능하므로 낙관적 락이 적합하며, 서버 부하도 줄일 수 있음. 
+ 분산 환경에서는 Redis 기반 분산 락을 도입해 동시성 제어와 확장성을 확보할 수 있음.

[RESULT]
```java
int threadCount = 100;
// 테스트 전에 쿠폰 초기화 (수량 10)
Coupon coupon = couponRepository.save(Coupon.builder()
        .discountPrice(1000L)
        .quantity(10)
        .build());
couponId = coupon.getId();
```
INFO 35208 --- [hhplus] [           main] k.h.e.c.coupon.CouponConcurrencyTest     : ✅ 성공: 10, 실패: 90

낙관적 락 적용


### 3. 재고 차감

[AS-IS]

동일한 상품에 대한 주문이 동시에 반복적으로 수행될 경우 재고 차감이 정상적으로 이루어지지 않음
```java
public interface ProductStockJpaRepository extends JpaRepository<ProductStock, Long> {
    List<ProductStock> findByProductId(Long productId);

}
```

[TO-BE]

```java

public interface ProductStockJpaRepository extends JpaRepository<ProductStock, Long> {
  List<ProductStock> findByProductId(Long productId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT ps FROM ProductStock ps WHERE ps.id = :id")
  Optional<ProductStock>  findByIdWithPessimisticLock(@Param("id") Long id);
}
```
+ 재고는 정합성이 중요한 데이터이므로, 충돌 시 재시도 부담이 큰 낙관적 락보다는 비관적 락이 적합함.
+ 비관적 락은 구현이 단순하고 데이터 일관성을 우선시할 수 있어 재고 관리에 유리함.


[RESULT]
```java
@BeforeEach
void beforeEach() {
    soccerUniform = productRepository.save(new Product("나이키", "축구 유니폼"));
    homeJersey = productStockRepository.save(new ProductStock(soccerUniform.getId(), "홈 저지", 89_000L, 80L));
}
```
INFO 20452 --- [hhplus] [           main] k.h.e.c.stock.StockConcurrencyTest       : ✅ 재고: 0

INFO 20452 --- [hhplus] [           main] k.h.e.c.stock.StockConcurrencyTest       : ✅ 성공: 80, 실패: 20
비관적 락 적용

---

## ✅ 한계점

### 1. 잔액 충전 및 차감
Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): 
 
낙관적 락(Optimistic Lock)의 경우 실패한 트랜잭션에 대한 재시도를 자동 처리하고있지 않아 애플리케이
션 수준에서의 트랜잭션 retry로직의 추가가 필요합니다.

-> 재시도 로직 필요


### 2. 선착순 쿠폰 발급
분산 환경의 경우 각자 다른  객체를 생성하기 때문에 데이터 일관성에 문제가 발생할 수 있습니다.

-> 데이터 일관성을 보장할 수 있는 기술로의 전환이 필요 ( Redis, Kafka )


### 3. 재고 차감
재고는 정확히 관리 되어야 하지만 이커머스 에서는 정합성을 보장보다는 처리량을 더 중요시 할 것 같습니다.
둘 다 적정하게 수행능력을 지키면서 상호보완 해줄방법이 필요합니다.

---

## ✅ 정리

- **사용자 포인트 잔액** → Optimistic Lock
- **상품 재고 차감** → Optimistic Lock
- **선착순 쿠폰 발급** → Pessimistic Lock

---

## ✅ 향후 개선 사항

- 에러 메시지 분류/정리
- 모듈화 및 리팩토링
- Redis 기반 Coupon 발급 리팩토링
- 동시성 제어 기능에 대한 통합 테스트 강화
- 통계 데이터 -> ElasticSearch 연동 (도전)

---

