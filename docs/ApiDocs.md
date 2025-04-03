# e-commerce docs

1. [요구사항분석](./Requirements.md)
2. [시퀀스 다이어그램](./Sequence.md)
3. [플로우차트](./FlowChart)
4. [ERD](./ERD.md)
5. [API 명세 문서](./ApiDocs.md)


# API 명세 문서

> ###  목차
> - [잔액 충전 API](#잔액-충전-API)
> - [잔액 조회 API](#잔액-조회-API)
> - [상품 조회 API](#상품-조회-API)
> - [상위 TOP5 상품 조회 API](#상위-TOP5-상품-조회-API)
> - [쿠폰 발급 API](#쿠폰-발급-API)
> - [쿠폰 조회 API](#쿠폰-조회-API)
> - [주문 결제 API](#주문-결제-API)
---

## 잔액 충전 API

### Request

- **Method** : POST
- **Endpoint** : `/api/users/{userId}/account`
- **Description** : 유저의 잔액을 충전하고 충전 결과를 반환 합니다.
- **Path Parameter** : `userId` - 충전할 유저의 고유 ID

### Request Body
```json
{
  "volume" : 1000
}
``` 

### Response Body
```json
{
  "code": 200,
  "message": "OK",
  "data": {
    "user_id": 1,
    "volume": 5000
  }
}
```


### Error
```json
{
  "code": 404,
  "message": "user not found"
}
```


---
## 잔액 조회 API

### Request

- **Method** : GET
- **Endpoint** : `/api/users/{userId}/account`
- **Description** : 유저 잔액을 반환 한다.
- **Path Parameter** : `userId` - 충전할 유저의 고유 ID

### Response Body
```json
{
  "code": 200,
  "message": "OK",
  "data": {
    "user_id": 1,
    "volume": 2000
  }
}
```
### Error
```json
{
  "code": 404,
  "message": "user not found"
}
```

---
## 상품 조회 API

### Request

- **Method** : GET
- **Endpoint** : `/api/products/{productId}`
- **Description** : 상품의 정보를 반환 한다.
- **Path Parameter** : `productId` - (필수) 조회할 상품의 ID 입니다.

### Response Body
```json
{  
  "code": 200,
  "message": "OK",
    "data": {
      "productId": 12,
      "name": "상품명",
      "brand": "브랜드",
      "stock": 100,
      "price": 10000,
      "stockQuantities": [
        {
          "size": "L",
          "quantity": 2
        },
        {
          "size": "M",
          "quantity": 3
        }
      ]
    }
}
```

### Error
```json
{
  "code": 404,
  "message": "product not found"
}
```

---
## 상위 TOP5 상품 조회 API

### Request

- **Method** : GET
- **Endpoint** : `/api/products/top`
- **Description** : 지정된 기간 동안 가장 많이 판매된 상위 N개의 상품을 조회합니다.
- **Query Parameter** :
    - limit : 조회할 상품의 개수, 예를 들어 limit=5로 설정 시 상위 5개 조회



### Response Body (limit 수만큼 상품 정보 반복)
```json
{
  "code": 200,
  "message": "OK",
  "topProducts": [
    {
      "productId": 123,
      "name": "상품명1"
    },
    {
      "productId": 456,
      "name": "상품명2"
    }
  ]
}
```

### Error
```json
{
  "code": 400,
  "message": "invalid parameters"
}
```

---
## 쿠폰 발급 API

### Request

- **Method** : POST
- **Endpoint** : `/api/users/{usedId}/coupons`
- **Description** : 쿠폰발급

### Request Body
```json
{
  "couponId": 1
}
```

### Response Body
```json
{
  "code": 200,
  "message": "OK"
}
```
### Error
```json
{
  "code": 400,
  "message": "coupon is end"
}
```
---
## 쿠폰 조회 API

### Request

- **Method** : GET
- **Endpoint** : `/api/users/{userId}/coupons`
- **Description** : 사용가능한 보유 쿠폰 목록을 조회한다.


### Response Body
```json
{
  "code": 200,
  "message": "OK",
  "data": {
    "coupons": [
      {
        "id": 1,
        "name": "쿠폰명",
        "discountRate": 0.1,
        "expired": "2026-01-01"
      }
    ]
  }
}
```
### Error
```json
{
  "code": 400,
  "message": "coupon is expired"
}
```

### Error
```json
{
  "code": 404,
  "message": "coupon is not exist"
}
```
---
## 주문 결제 API

### Request

- **Method** : POST
- **Endpoint** : `/api/orders`
- **Description** : 사용자가 선택한 상품을 주문하고, 보유 잔액에서 결제 금액을 차감하여 결제를 완료합니다.

### Request Body
```json
{
  "userId": 1,
  "couponId": 1,
  "productStocks": [
    {
      "id": 123,
      "quantity": 2
    },
    {
      "id": 456,
      "quantity": 1
    }
  ]
}
```

### Response Body
```json
{
  "code": 200,
  "message": "OK"
}
```
### Error
```json
{
  "code": 404,
  "message": "product not found"
}
```

### Error
```json
{
  "code": 500,
  "message": "user not money"
}
```

---

