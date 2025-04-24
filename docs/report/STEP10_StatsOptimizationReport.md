

## ✅ 인기 상품 랭킹 개선 → Elasticsearch 집계 활용

### 📌 문제 정의
현재 인기 상품 조회 API는 최근 3일간 주문 데이터를 기준으로 GROUP BY, SUM, ORDER BY 등을 활용한 RDBMS 집계 쿼리로 구현되어 있습니다.
예상되는 문제는 다음과 같습니다:

+ 대용량 주문 데이터가 지속적으로 누적됨에 따라, 필터링, 집계, 정렬 성능이 점차 저하될 가능성.

 
+ 트래픽 급증 시 RDBMS 자원(CPU/메모리/I/O) 과부하 유발


+ 실시간 집계 공이 어려워 마케팅/추천 시스템 대응에 한계

```java
-- 예시 쿼리
SELECT oi.product_option_id, SUM(oi.quantity) AS totalSaleQuantity
FROM order_item oi
WHERE oi.created_at >= DATE_SUB(NOW(), INTERVAL 3 DAY)
GROUP BY oi.product_option_id
ORDER BY totalSaleQuantity DESC
LIMIT 5;

이러한 방식은 **운영용 트랜잭션 DB(OLTP)**에서 **분석용 쿼리(OLAP)**를 수행하는 방식으로,
정확하지만 성능상 확장에 한계가 있으며, 실시간성에도 제약이 있습니다.
```
### 🎯개선 방향
+ Kafka 등 이벤트 기반 비동기 처리로 Elasticsearch에 데이터 적재
+ Elasticsearch는 대량 데이터에 대한 빠른 검색 및 집계에 최적화된 분산형 검색엔진으로,  다음과 같은 개선 방향을 제안합니다:

Elasticsearch의 terms aggregation, date_histogram 활용하여 실시간 랭킹 계산

```java
ES
{
  "query": {
    "range": {
      "registeredAt": {
        "gte": "now-3d/d"
      }
    }
  },
  "aggs": {
    "daily": {
      "date_histogram": {
        "field": "registeredAt",
        "calendar_interval": "day"
      },
      "aggs": {
        "top_products": {
          "terms": {
            "field": "productOptionId",
            "size": 5
          }
        }
      }
    }
  }
}
range.query: 최근 3일 데이터를 필터링

date_histogram: 일자별로 버킷 생성

terms: 각 일자별로 가장 많이 팔린 상품 옵션 상위 5개를 집계
```


### ✅ 기대 효과
| 항목       | 기존 RDBMS 집계                    | Elasticsearch 기반            |
|------------|------------------------------------|-------------------------------|
| 응답 속도  | 평균 3~5초                         | 200ms 이하                    |
| 실시간성   | 낮음 (배치 필요)                   | 높음 (비동기 적재)            |
| 확장성     | 낮음 (트래픽 증가 시 병목)         | 높음 (수평 확장 가능)         |
| 운영 부담  | 높음 (DB 부하)                     | 낮음 (ES 분산 처리)           |

- 실시간 인기 상품 제공
- 트래픽 급증에도 안정적 처리
- 운영 리소스 최적화 + 분석 활용도 향상


### 🎯 목표 수치
- 응답 시간: `3~5초 → 200ms 이하`
- 배치 집계 제거
- 피크 시 99.9% 응답 SLA 유지


### 📌 결론
Elasticsearch를 활용한 실시간 랭킹 분석으로 병목 문제를 해소하고, 고성능 사용자 경험을 안정적으로 제공할 수 있습니다.  
→ 단, ES는 RDBMS와는 다른 데이터 모델과 쿼리 방식을 사용하므로 도입 전 학습이 필요합니다. (실무에서 관련 kafka, ELK, Apache NiFi  등 사용하고 있습니다.)  
---

