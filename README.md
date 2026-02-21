# lotto-event-system

모바일팩토리 로또 이벤트 시스템 과제

---

## 1. 프로젝트 개요

10,000명 대상 로또 이벤트 시스템 구현

- 이벤트 기간: 2025-02-01 ~ 2025-03-31
- 발표 기간: 2025-04-01 ~ 2025-04-15
- 총 당첨자: 1,000명
    - 1등: 1명
    - 2등: 5명
    - 3등: 44명
    - 4등: 950명

---

## 2. 기술 스택

- Java 17
- Spring Boot
- Spring Data JPA
- MariaDB
- Thymeleaf

---

## 3. 데이터베이스 설계

(추후 ERD 이미지 추가 예정)

주요 테이블:
- event
- event_sequence
- participant
- entry
- winner_slot
- result
- result_view
- sms_log

---

## 4. 실행 방법

1. MariaDB 실행
2. lotto 데이터베이스 생성
3. application.yml DB 정보 설정
4. Spring Boot 실행

---

## 5. 설계 핵심 포인트

- 이벤트별 entry_no 시퀀스 관리
- 휴대폰 기준 중복 참여 방지
- 사전 winner_slot 생성으로 당첨 분배 보장
- 결과 최초 조회 시 등수 공개, 이후 당첨 여부만 표시