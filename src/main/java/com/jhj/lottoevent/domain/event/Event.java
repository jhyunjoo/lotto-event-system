package com.jhj.lottoevent.domain.event;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event")
public class Event {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "event_start_at", nullable = false)
    private LocalDateTime eventStartAt;

    @Column(name = "event_end_at", nullable = false)
    private LocalDateTime eventEndAt;

    @Column(name = "announce_start_at", nullable = false)
    private LocalDateTime announceStartAt;

    @Column(name = "announce_end_at", nullable = false)
    private LocalDateTime announceEndAt;

    @Column(name = "winning_number", nullable = false, length = 6)
    private String winningNumber;

    @Column(name = "fixed_first_phone", nullable = false, length = 13)
    private String fixedFirstPhone;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants = 10000;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

}