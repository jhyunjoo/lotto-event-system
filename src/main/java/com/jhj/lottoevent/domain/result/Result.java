package com.jhj.lottoevent.domain.result;

import com.jhj.lottoevent.domain.entry.Entry;
import com.jhj.lottoevent.domain.event.Event;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "result",
        uniqueConstraints = @UniqueConstraint(name = "uk_result_event_entry", columnNames = {"event_id", "entry_id"})
)
public class Result {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entry_id")
    private Entry entry;

    @Column(nullable = false)
    private Byte rank; // 0=꽝, 1~4

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public void setEvent(Event event) { this.event = event; }

    public void setEntry(Entry entry) {  this.entry = entry; }

    public void setRank(Byte rank) { this.rank = rank;  }
}