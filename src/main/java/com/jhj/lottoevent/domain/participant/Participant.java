package com.jhj.lottoevent.domain.participant;

import com.jhj.lottoevent.domain.event.Event;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "participant",
        uniqueConstraints = @UniqueConstraint(name = "uk_participant_event_phone", columnNames = {"event_id", "phone"})
)
public class Participant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(nullable = false, length = 13)
    private String phone; // 010-0000-0000

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public void setVerifiedAt(LocalDateTime verifiedAt) {  this.verifiedAt = verifiedAt; }
    public LocalDateTime getVerifiedAt() {  return verifiedAt; }
}