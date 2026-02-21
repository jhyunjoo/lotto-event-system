package com.jhj.lottoevent.domain.entry;

import com.jhj.lottoevent.domain.event.Event;
import com.jhj.lottoevent.domain.participant.Participant;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "entry",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_entry_event_entry_no", columnNames = {"event_id", "entry_no"}),
                @UniqueConstraint(name = "uk_entry_event_participant", columnNames = {"event_id", "participant_id"})
        }
)
public class Entry {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @Column(name = "entry_no", nullable = false)
    private Integer entryNo;

    @Column(name = "issued_lotto_number", nullable = false, length = 6)
    private String issuedLottoNumber;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public Participant getParticipant() { return participant; }
    public void setParticipant(Participant participant) { this.participant = participant; }
    public Integer getEntryNo() { return entryNo; }
    public void setEntryNo(Integer entryNo) { this.entryNo = entryNo; }
    public void setIssuedLottoNumber(String issuedLottoNumber) { this.issuedLottoNumber = issuedLottoNumber;}
    public String getIssuedLottoNumber() { return issuedLottoNumber; }
}