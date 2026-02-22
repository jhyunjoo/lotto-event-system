package com.jhj.lottoevent.domain.result;

import com.jhj.lottoevent.domain.entry.Entry;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "result_view")
public class ResultView {

    @Id
    @Column(name = "entry_id")
    private Long entryId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entry_id")
    private Entry entry;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "first_viewed_at")
    private LocalDateTime firstViewedAt;

    @Column(name = "last_viewed_at")
    private LocalDateTime lastViewedAt;

    public Long getEntryId() { return entryId; }
    public Entry getEntry() { return entry; }
    public void setEntry(Entry entry) { this.entry = entry; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
}