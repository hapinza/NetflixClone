package io.github.catimental.diexample.domain.event;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.persistence.UniqueConstraint;



@Entity
@Table(name = "failed_events")
public class FailedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String eventId;

    @Lob
    @Column(nullable = false)
    private String payload;


    @Lob
    @Column(nullable = false)
    private String reason;


    @Column(nullable = false)
    private int retryCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FailedEventStatus status = FailedEventStatus.FAILED;

    @Column(nullable = false)
    private LocalDateTime failedAt;

    private LocalDateTime lastRetriedAt;
    private LocalDateTime recoveredAt;

    protected FailedEvent(){}

    public FailedEvent(String eventId, String payload, String reason, int retryCount, FailedEventStatus status){
        this.eventId = eventId;
        this.payload = payload;
        this.reason = reason;
        this.retryCount = retryCount;
        this.status = status;
        this.failedAt = LocalDateTime.now();
    }

    public void makeRetried(){
        this.retryCount++;
        this.lastRetriedAt = LocalDateTime.now();
    }

    public void makeRecovered(){
        this.status = FailedEventStatus.RECOVERED;
        this.recoveredAt = LocalDateTime.now();
    }

    public int getRetryCount(){
        return this.retryCount;
    }

    public void setStatus(FailedEventStatus status){
        this.status = status;
    }

    public FailedEventStatus getStatus(){
        return this.status;
    }

    public String getPayload(){
        return this.payload;
    }

    public String getEventId(){
        return this.eventId;
    }

    public Long getId()
    {
        return this.id;
    }
    
}
