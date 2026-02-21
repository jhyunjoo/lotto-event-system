package com.jhj.lottoevent.domain.event;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "winner_slot",
        uniqueConstraints = @UniqueConstraint(name = "uk_winner_slot_event_entry_no", columnNames = {"event_id", "entry_no"})
)
public class WinnerSlot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "entry_no", nullable = false)
    private Integer entryNo;

    @Column(nullable = false)
    private Byte rank; // 1~4

    @Column(name = "reserved_at", nullable = false)
    private LocalDateTime reservedAt = LocalDateTime.now();

    public Byte getRank() { return rank; }
}