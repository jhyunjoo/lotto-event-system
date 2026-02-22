package com.jhj.lottoevent.domain.sms;

import com.jhj.lottoevent.domain.entry.Entry;
import com.jhj.lottoevent.domain.event.Event;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sms_log")
public class SmsLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sms_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(nullable = false, length = 13)
    private String phone;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false, length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id")
    private Entry entry; // nullable

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();

    public void setEvent(Event event) { this.event = event; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setType(String type) { this.type = type; }

    public void setMessage(String message) { this.message = message; }
    public String getMessage() { return message; }

    public void setStatus(String status) { this.status = status; }
    public void setEntry(Entry entry) { this.entry = entry; }
}