package io.github.catimental.diexample.domain.event;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "outbox")
public class OutboxEvent {
    
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(name = "event_id", nullable = false, unique = true)
private String eventId;

/*
ex)
aggregateType = "MOVIE"
aggregateId = "123"

ex)
aggregateType = "USER_ACTIVITY"
aggregateId = "456"
*/
@Column(name = "aggregate_type", nullable = false)
private String aggregateType;

@Column(name = "aggregate_id", nullable = false)
private String aggregateId;

@Column(name= "event_type", nullable = false)
private String eventType;

@Lob
@Column(name = "payload", nullable = false)
private String payload;

@Column(name = "status", nullable= false)
private String status;

@Column(name = "retry_count", nullable = false)
private int retryCount;

// when to restart / resend
@Column(name= "next_retry_at", nullable = false)
private LocalDateTime nextRetryAt;

@Column(name = "created_at", nullable = false)
private LocalDateTime createdAt;


// responsibilty for outbox
@Column(name = "sent_at")
private LocalDateTime sentAt;

@Lob
@Column(name = "last_error")
private String lastError;

protected OutboxEvent(){}


public OutboxEvent(
    String aggregateType,
    String aggregateId,
    String eventType,
    String payload
){
    this.eventId = java.util.UUID.randomUUID().toString();
    this.aggregateType = aggregateType;
    this.aggregateId = aggregateId;
    this.eventType = eventType;
    this.payload = payload;
    this.status = "PENDING";
    this.retryCount = 0;
    this.nextRetryAt = LocalDateTime.now();
    this.createdAt = LocalDateTime.now();
}

public void markSent(){
    this.status = "SENT";
    this.sentAt = LocalDateTime.now();
    this.lastError = null;
}


public void markRetry(String errorMessage, LocalDateTime nextRetryAt){
    this.retryCount++;
    this.status = "PENDING";
    this.lastError = errorMessage;
    this.nextRetryAt = nextRetryAt;
}

public void markFailed(String errorMessage){
    this.retryCount++;
    this.status = "FAILED";
    this.lastError = errorMessage;
}

public Long getId(){return this.id;}
public String getEventId(){return this.eventId;}
public String getAggregateType(){return this.aggregateType;}
public String getAggregateId(){return this.aggregateId;}
public String getEventType(){return this.eventType;}
public String getPayload(){return this.payload;}
public String getStatus(){return this.status;}
public int getRetryCount(){return this.retryCount;}
public LocalDateTime getNextRetryAt(){return nextRetryAt;}
public String getMessage(){return this.lastError;}
}
