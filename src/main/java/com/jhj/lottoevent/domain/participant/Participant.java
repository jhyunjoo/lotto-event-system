package com.jhj.lottoevent.domain.participant;

import com.jhj.lottoevent.domain.event.Event;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "participant",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_participant_event_phone", columnNames = {"event_id", "phone"})
        }
)
public class Participant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verify_code", length = 6)
    private String verifyCode;

    @Column(name = "verify_expires_at")
    private LocalDateTime verifyExpiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // getters/setters
    public Long getId() { return id; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public String getVerifyCode() { return verifyCode; }
    public void setVerifyCode(String verifyCode) { this.verifyCode = verifyCode; }

    public LocalDateTime getVerifyExpiresAt() { return verifyExpiresAt; }
    public void setVerifyExpiresAt(LocalDateTime verifyExpiresAt) { this.verifyExpiresAt = verifyExpiresAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}