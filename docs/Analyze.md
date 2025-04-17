
---

## ✅ 주문 조회 개선

### 📌 문제 정의
 주문 조회 시, `JOIN`, `ORDER BY`, `LIMIT` 쿼리가 대량 데이터에서 반복적으로 수행되어  
디스크 I/O와 CPU 부하가 증가하며, 동시 접속 시 락 경합으로 인해 전체 응답 속도가 느려지는 병목 현상이 발생합니다.

### 🎯 대상 선정
주문(Order), 주문상품(OrderItem) 테이블을 기반으로 동작하는 마이페이지 API는  
사용자별 반복 조회가 많아 성능 저하가 자주 발생하는 주요 개선 대상입니다.

### 📊 측정 방식
- `EXPLAIN`으로 인덱스 활용 여부 및 풀스캔 여부 분석
- 슬로우 쿼리 로그 기반 응답 시간/지연 원인 분석
- InnoDB 락 대기 및 커넥션 경합 현황 추적

### 🔁 비교 및 개선
#### 기존
- 주문, 주문상품 테이블 조인 후 정렬 및 페이징 처리

#### 개선
- `(user_id, created_at)` 복합 인덱스 추가
- OrderSummary 테이블로 핵심 정보만 서머리화
- 월 단위 파티셔닝 도입으로 범위 쿼리 최적화
- `ConcurrentHashMap`으로 사용자별 캐싱 → 중복 조회 최소화

```java
// 사용자별 주문 캐시 (중복 조회 방지)
private final ConcurrentHashMap<Long, List<OrderSummary>> orderCache = new ConcurrentHashMap<>();

public List<OrderSummary> getRecentOrders(Long userId) {
    return orderCache.computeIfAbsent(userId, this::queryRecentOrdersFromDb);
}
```

### 🎯 기대 효과
- 트랜잭션 일관성 보장 + 조회 성능 향상
- 구조 단순화로 유지보수 및 확장성 향상
- 캐싱 기반 요청 분산으로 서버 자원 최적화

### 📌 결론
RDBMS 인덱스 구조 최적화 + Java 수준의 동시성 제어 기법을 통해  
Elasticsearch 없이도 안정적인 고성능 마이페이지 조회를 구현할 수 있습니다.

---

## ✅ 인기 상품 랭킹 개선 → Elasticsearch 집계 활용

### 📌 문제 정의
`GROUP BY`, `COUNT` 쿼리로 인기 상품을 집계하는 기존 방식은 대용량 데이터에 취약하여 실시간 제공이 어렵고,  
트래픽 급증 시 시스템 자원을 과다 소모합니다.

### 🎯 대상 선정
주문상품(OrderItem) 테이블을 기반으로 일/주/월 단위 랭킹을 계산하는 인기 상품 API는  
마케팅 및 트렌드 기반 추천에 있어 핵심 역할을 수행합니다.

### 📊 측정 방식
- 집계 쿼리 수행 시간 분석
- API 호출 빈도 + CPU/I/O 자원 사용량 추적
- 배치 집계 주기 분석

### 🔁 비교 및 교환
- 주문 데이터를 Kafka 또는 이벤트 기반으로 ES에 비동기 적재
- `terms aggregation` + `date_histogram` 활용하여 실시간 랭킹 계산

```json
// Elasticsearch 집계 쿼리 예시
{
  "aggs": {
    "top_products": {
      "terms": { "field": "productId", "size": 10 }
    }
  }
}
```

### 🎯 목표 수치
- 응답 시간: `3~5초 → 200ms 이하`
- 배치 집계 제거
- 피크 시 99.9% 응답 SLA 유지

### ✅ 기대 효과
- 실시간 인기 상품 제공
- 트래픽 급증에도 안정적 처리
- 운영 리소스 최적화 + 분석 활용도 향상

### 📌 결론
Elasticsearch를 활용한 실시간 랭킹 분석으로 병목 문제를 해소하고, 고성능 사용자 경험을 안정적으로 제공할 수 있습니다.  
→ 단, ES는 RDBMS와는 다른 데이터 모델과 쿼리 방식을 사용하므로 도입 전 학습이 필요합니다.

---

