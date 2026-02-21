package com.jhj.lottoevent.domain.event;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_sequence")
public class EventSequence {

    @Id
    @Column(name = "event_id")
    private Long eventId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "next_entry_no", nullable = false)
    private Integer nextEntryNo;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Long getEventId() { return eventId; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public Integer getNextEntryNo() { return nextEntryNo; }
    public void setNextEntryNo(Integer nextEntryNo) { this.nextEntryNo = nextEntryNo; }
}