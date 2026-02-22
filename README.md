# lotto-event-system
모바일팩토리 로또 이벤트 시스템 과제

## 1. 프로젝트 개요
10,000명 대상 로또 이벤트 시스템 구현

- 이벤트 기간: 2025-02-01 ~ 2025-03-31
- 발표 기간: 2025-04-01 ~ 2025-04-15
- 총 당첨자: 1,000명
    - 1등: 1명
    - 2등: 5명
    - 3등: 44명
    - 4등: 950명

## 2. 기술 스택
- Java 17
- Spring Boot
- Spring Data JPA (Hibernate)
- MariaDB
- Thymeleaf

## 3. 주요 기능
- 휴대폰 인증 후 참가 가능 (미인증 시 인증 페이지로 이동)
- 이벤트별 entry_no 발급 (event_sequence + PESSIMISTIC LOCK)
- 휴대폰 번호 기준 중복 참여 방지 (event_id + phone UNIQUE)
- 사전 winner_slot 생성으로 당첨 분배 보장 (1등/2등/3등/4등 인원 고정)
- 결과 조회 정책
    - 최초 조회: 등수 공개
    - 2회차부터: 당첨/미당첨만 노출
- 문자 발송은 실제 발송 대신 sms_log 기록으로 대체

## 4. 화면(URL)
- 참가: `/event/join`
- 휴대폰 인증: `/auth`
- 결과 확인: `/result/check`

## 5. 데이터베이스 설계
주요 테이블:
- event, event_sequence
- participant, entry
- winner_slot, result, result_view
- sms_log

> 참고: `sql/lotto_schema.sql`은 DDL 참고용입니다.  
> 실행은 JPA ddl-auto에 의해 자동 생성되도록 구성했습니다.

## 6. 실행 방법
1) MariaDB 실행 후 DB 생성
```sql
CREATE DATABASE lotto DEFAULT CHARACTER SET utf8mb4;