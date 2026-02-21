SET NAMES utf8mb4;

-- 1) event
CREATE TABLE IF NOT EXISTS event (
  event_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  event_start_at DATETIME NOT NULL,
  event_end_at DATETIME NOT NULL,
  announce_start_at DATETIME NOT NULL,
  announce_end_at DATETIME NOT NULL,
  winning_number CHAR(6) NOT NULL,
  fixed_first_phone CHAR(13) NOT NULL COMMENT '1등 고정 당첨 휴대폰 (010-0000-0000)',
  max_participants INT NOT NULL DEFAULT 10000,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 이벤트별 entry_no 발급용 시퀀스 (PK=FK, 식별 관계)
CREATE TABLE IF NOT EXISTS event_sequence (
  event_id BIGINT PRIMARY KEY,
  next_entry_no INT NOT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_event_sequence_event
    FOREIGN KEY (event_id) REFERENCES event(event_id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- 2) participant (휴대폰 기준, 이벤트별 1회만 참여)
CREATE TABLE IF NOT EXISTS participant (
  participant_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  phone CHAR(13) NOT NULL COMMENT '010-0000-0000 형식 저장',
  verified_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_participant_event
    FOREIGN KEY (event_id) REFERENCES event(event_id)
    ON DELETE CASCADE,
  CONSTRAINT uk_participant_event_phone UNIQUE (event_id, phone)
) ENGINE=InnoDB;

CREATE INDEX ix_participant_event_phone ON participant(event_id, phone);

-- 3) entry (참가 기록)
CREATE TABLE IF NOT EXISTS entry (
  entry_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  participant_id BIGINT NOT NULL,
  entry_no INT NOT NULL,
  issued_lotto_number CHAR(6) NOT NULL,
  issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_entry_event
    FOREIGN KEY (event_id) REFERENCES event(event_id)
    ON DELETE CASCADE,
  CONSTRAINT fk_entry_participant
    FOREIGN KEY (participant_id) REFERENCES participant(participant_id)
    ON DELETE CASCADE,
  CONSTRAINT uk_entry_event_entry_no UNIQUE (event_id, entry_no),
  CONSTRAINT uk_entry_event_participant UNIQUE (event_id, participant_id)
) ENGINE=InnoDB;

CREATE INDEX ix_entry_event_participant ON entry(event_id, participant_id);
CREATE INDEX ix_entry_event_entry_no ON entry(event_id, entry_no);

-- 4) winner_slot (사전 당첨 계획)
CREATE TABLE IF NOT EXISTS winner_slot (
  slot_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  entry_no INT NOT NULL,
  rank TINYINT NOT NULL COMMENT '1~4등',
  reserved_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_winner_slot_event
    FOREIGN KEY (event_id) REFERENCES event(event_id)
    ON DELETE CASCADE,
  CONSTRAINT uk_winner_slot_event_entry_no UNIQUE (event_id, entry_no)
) ENGINE=InnoDB;

CREATE INDEX ix_winner_slot_event_rank ON winner_slot(event_id, rank);

-- 5) result (당첨 결과)
CREATE TABLE IF NOT EXISTS result (
  result_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  entry_id BIGINT NOT NULL,
  rank TINYINT NOT NULL COMMENT '0=꽝, 1~4등',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_result_event
    FOREIGN KEY (event_id) REFERENCES event(event_id)
    ON DELETE CASCADE,
  CONSTRAINT fk_result_entry
    FOREIGN KEY (entry_id) REFERENCES entry(entry_id)
    ON DELETE CASCADE,
  CONSTRAINT uk_result_event_entry UNIQUE (event_id, entry_id)
) ENGINE=InnoDB;

CREATE INDEX ix_result_event_rank ON result(event_id, rank);

-- 6) result_view (결과 확인 횟수 관리, PK=FK 식별 관계)
CREATE TABLE IF NOT EXISTS result_view (
  entry_id BIGINT PRIMARY KEY,
  view_count INT NOT NULL DEFAULT 0,
  first_viewed_at DATETIME NULL,
  last_viewed_at DATETIME NULL,
  CONSTRAINT fk_result_view_entry
    FOREIGN KEY (entry_id) REFERENCES entry(entry_id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7) sms_log (문자 발송 이력)
CREATE TABLE IF NOT EXISTS sms_log (
  sms_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  phone CHAR(13) NOT NULL COMMENT '010-0000-0000 형식 저장',
  type VARCHAR(30) NOT NULL COMMENT 'VERIFY_CODE / ISSUE_NUMBER / REMIND_UNCHECKED',
  message VARCHAR(500) NOT NULL,
  status VARCHAR(20) NOT NULL COMMENT 'SUCCESS / FAIL',
  entry_id BIGINT NULL COMMENT '관련 참가 기록 (선택)',
  sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_sms_log_event
    FOREIGN KEY (event_id) REFERENCES event(event_id)
    ON DELETE CASCADE,
  CONSTRAINT fk_sms_log_entry
    FOREIGN KEY (entry_id) REFERENCES entry(entry_id)
    ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX ix_sms_log_event_phone_sent_at ON sms_log(event_id, phone, sent_at);
CREATE INDEX ix_sms_log_entry_id ON sms_log(entry_id);