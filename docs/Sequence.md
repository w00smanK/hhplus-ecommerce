# e-commerce docs

1. [요구사항분석](./Requirements.md)
2. [시퀀스 다이어그램](./Sequence.md)
3. [플로우차트](./FlowChart)
4. [ERD](./ERD.md)
5. [API 명세 문서](./ApiDocs.md)

### 시퀀스 다이어그램


> ###  목차
> - [주문 API](#주문)
> - [결제 API](#결제)
> - [쿠폰 발급 API](#쿠폰-발급)
> - [잔액 충전 API](#잔액-충전)
> - [상위 TOP5 상품 조회 API](#상위-TOP5-상품-조회)
---


> [정책]  
> 기본 사용자 : 잔액 0원 / 쿠폰 0개 / 주문 0건 <br>
> 쿠폰 발급 정책 : 선착순 쿠폰 발급 <br>
> 비로그인 유저 없음 <br>
> 
> * 단순조회는 생략했습니다. 상품 조회 / 잔액 조회 

### 주문
```mermaid
sequenceDiagram
    actor 사용자
    participant 주문
    participant 사용자쿠폰
    participant 상품
    participant 결제

    autonumber
    사용자 ->>+ 주문: 주문 요청
    주문 ->>+ 상품: 상품 조회
    deactivate 주문
    상품 ->> 상품: 재고 확인
    alt 상품 재고 부족
        상품 -->> 주문: [주문 실패]
        주문 -->> 사용자: 예외 메시지[재고 부족]
    end
    상품 -->> 주문 : 상품 정보 전달
    activate 주문
    deactivate 상품

    alt 쿠폰 사용
        주문 ->>+ 사용자쿠폰: 쿠폰 조회
        deactivate 주문
        사용자쿠폰 ->> 사용자쿠폰: 유효 쿠폰 검증
        alt 쿠폰 검증 실패
            사용자쿠폰 -->>- 주문: 예외 메시지[유효하지 않는 쿠폰]
        end
    end
    activate 주문
    주문 ->>+ 결제: 결제 진행
    deactivate 주문

```
<br>
---

### 결제
```mermaid
sequenceDiagram
    actor 사용자
    participant 결제
    participant 주문
    participant 잔액
    participant 데이터 플랫폼

    autonumber
    사용자 ->>+ 결제: 결제 요청
    결제 ->>+ 주문: 주문 정보 조회
    deactivate 결제
    주문 -->>- 결제: 주문 정보 반환
    activate 결제
    결제 ->>+ 잔액: 포인트 차감 요청
    deactivate 결제
    잔액 ->> 잔액: 포인트 조회
    잔액 -->> 결제: 포인트 조회 완료

    alt 잔액 부족
        잔액 -->> 사용자: 예외 메시지 [잔액 부족]
    end

    잔액 -->>- 결제: 잔액 정보 반환
    결제 ->> 결제: 결제 상태 완료 처리
    결제 -->> 데이터 플랫폼: 데이터 전송(결제 히스토리 저장)
    결제 -->> 데이터 플랫폼: 데이터 전송(주문/상품 히스토리 저장)
    결제 -->> 사용자: 결제 완료 응답
```
<br>

---

### 쿠폰 발급
```mermaid
sequenceDiagram
    autonumber
    actor 사용자
    participant 쿠폰
    participant 사용자쿠폰
    사용자 ->>+ 쿠폰: 쿠폰 발급 요청
    쿠폰 ->>+ 쿠폰: 쿠폰 조회

    alt 선착순 쿠폰 종료
        쿠폰 -->>- 사용자: 예외 메세지[ 쿠본 발급 종료 ]
    end


    쿠폰 ->>+ 쿠폰: 쿠폰 발급
    쿠폰 ->>+ 사용자쿠폰: 쿠폰 저장
    사용자쿠폰 -->>- 쿠폰: 쿠폰 저장 완료
    쿠폰 -->>- 사용자: 쿠폰 발급 성공
```
<br>

---
###  잔액 충전
```mermaid
sequenceDiagram
    autonumber
    actor 사용자
    participant 잔액
    participant 데이터플랫폼

    사용자 ->>+ 잔액: 잔액 충전 요청


    잔액 ->> 잔액: 잔액 조회
    Note right of 잔액: [정책]:비로그인 시는 생각하지 않고 사용자의 초기 잔액은 0
    잔액 ->> 잔액: 잔액 충전
    잔액 ->> 잔액: 잔액 저장

    잔액 ->>+ 데이터플랫폼: 충전 내역 전송
    데이터플랫폼 -->>- 잔액: 전송 성공

    잔액 -->>- 사용자: 잔액 충전

```
<br>

---
### 상위 TOP5 상품 조회

```mermaid
sequenceDiagram
    participant 사용자
    participant 상품주문통계
    participant 데이터플랫폼

    사용자 ->>+ 상품주문통계: 주문 통계 조회 요청
    상품주문통계 ->>+ 데이터플랫폼: 데이터 요청
    데이터플랫폼 -->>- 상품주문통계: 데이터 반환
    상품주문통계 -->>- 사용자: 통계 데이터 반환
```
<br>

---


<br><br>
