# e-commerce docs

1. [요구사항분석](./Requirements.md)
2. [시퀀스 다이어그램](./Sequence.md)
3. [플로우차트](./FlowChart)
4. [ERD](./ERD.md)
5. [API 명세 문서](./ApiDocs.md)


## ERD
```mermaid

erDiagram
user ||--o{ point : "1:1"
user ||--o{ issued_coupon : "1:N"
user ||--o{ order : "1:N"

point ||--o{ point_history : "1:N"
point_history ||--o| issued_coupon: "0..1:1"

coupon ||--o{ issued_coupon : "1:N"

payment ||--|| order : "1:1"
order ||--|{ order_item : "1:N"
order ||--o| issued_coupon: "0..1:1"
product ||--o{ product_stock : "1:N"
product_stock ||--o{ order_item : "1:N"

order_item ||--o{ product_order_stat : "1:N"

    user {
        BIGINT user_id PK "사용자 ID"
        VARCHAR name "사용자 이름"
        TIMESTAMP registered_dt "등록일시"
        TIMESTAMP updated_at "수정일시"
    }

    point {
        BiGINT point_id PK "잔고 ID"
        BIGINT user_id FK "사용자 ID"
        BIGINT amount "잔고 금액"
        TIMESTAMP registered_dt "등록일시"
        TIMESTAMP updated_at "수정일시"
    }

    point_history {
        BIGINT point_history_id PK "잔액 이력 ID"
        BIGINT user_id FK "사용자 ID"
        VARCHAR type "거래 유형 (충전, 사용)"
        BIGINT volume "거래 금액"
        TIMESTAMP registered_dt "등록일시"
        TIMESTAMP updated_at "수정일시"
    }

    coupon {
        BIGINT coupon_id PK "쿠폰 ID"
        VARCHAR name "쿠폰이름"
        VARCHAR coupon_status "쿠폰 상태"
%%        FLOAT discount_rate "할인율"
        TIMESTAMP expired_at "만료일시"
        INT quantity "쿠폰 수량"
        TIMESTAMP registered_dt "등록일시"
        TIMESTAMP updated_at "수정일시"
    }

    issued_coupon {
        BIGINT issued_id PK "사용자 쿠폰 ID"
        BIGINT user_id FK "사용자 ID"
        BIGINT coupon_id FK "쿠폰 ID"
        VARCHAR status "쿠폰 상태"
        TIMESTAMP issued_at "발급일시"
        TIMESTAMP expired_at "만료일시"
    }

    order {
        BIGINT order_id PK "주문 ID"
        BIGINT user_id FK "사용자 ID"
        BIGINT coupon_id FK "사용자 쿠폰 ID"
        vARCHAR status "주문 상태 (예: SUCCESS, FAIL)"
        BIGINT total_price "주문 총 금액"
        BIGINT discount_price "할인 금액"
        TIMESTAMP registered_dt "등록일시"
        TIMESTAMP updated_at "수정일시"
    }

    order_item {
        BIGINT order_item_id PK "주문 상품 ID"
        BIGINT order_id FK "주문 ID"
        BIGINT product_id FK "상품 ID"
        INT amount "상품 주문 수량"
        BIGINT price "상품 단가 가격"
        TIMESTAMP registered_dt "등록일시"
        TIMESTAMP updated_at "수정일시"
    }

    payment {
        BIGINT payment_id PK "결제 ID"
        BIGINT order_id FK "주문 ID"
        VARCHAR status "결제 상태 (예: SUCCESS, FAIL)"
        BIGINT amount "결제 금액"
        TIMESTAMP registered_dt "등록일시"
        TIMESTAMP updated_at "수정일시"
    }

    product {
        BIGINT product_id PK "상품 ID"
        VARCHAR name "상품명"
        BIGINT price "가격"
        TIMESTAMP registered_dt "등록일시"
        TIMESTAMP updated_at "수정일시"
    }

    product_stock {
        BIGINT id PK
        BIGINT product_id FK "상품 ID"
        VARCHAR option_value "옵션"
        BIGINT price "가격"
        BIGINT stock "재고"
        TIMESTAMP registered_dt "등록일시"
        TIMESTAMP updated_at "수정일시"
    }

    product_order_stat {
        BIGINT product_id PK "상품 ID"
        DATE stat_date "통계 일자"
        INT sale_count "판매 개수"
        TIMESTAMP registered_dt "등록일시"
        TIMESTAMP updated_at "수정일시"
    }

```