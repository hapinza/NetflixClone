![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3-green)
![Redis](https://img.shields.io/badge/Redis-Cache-red)
![Docker](https://img.shields.io/badge/Docker-Container-blue)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue)
![CI](https://img.shields.io/badge/GitHubActions-CI-black)


# Fault-Tolerant Backend System with Redis Streams, Rate Limiting & Load Testing

## Key Achievements

- Built a scalable backend system using Spring Boot, Redis, and MySQL
- Implemented Redis-based rate limiting using atomic Lua scripts
- Achieved p95 latency of 32ms under concurrent load (k6 testing)
- Designed stateless JWT authentication with refresh token rotation
- Implemented idempotent processing and atomic operations to eliminate race conditions and duplicate event issues in concurrent environments
- Built a failure recovery workflow with bulk retry and manual replay endpoints, achieving 70% recovery rate (7/10) while preventing infinite retries through bounded retry policies and dead-event handling
- Containerized system with Docker and automated CI using GitHub Actions
- Built a fault-tolerant event-driven backend system using Redis Streams, sustaining 15,000+ requests with 0% failure and zero data loss during simulated database outages.
- Implemented the Outbox Pattern to ensure consistency between database transactions and event streams, successfully recovering 1,000+ pending events without data loss.
  
---

## Why this project matters
This project focuses on understanding how backend systems behave under real-world traffic and designing solutions for scalability, performance, and reliability.

---

## Overview
This project simulates a production-style backend system for a streaming platform, focusing on scalability, performance, and real-world system behavior under load.

### The system focuses on:

- Object-oriented layered architecture
- JWT-based stateless authentication
- Redis-backed rate limiting
- Transactional database design
- CI automation and containerized deployment

---

## System Architecture

```
Client (React)
    |
    v
Spring Security Filter Chain
    |
    v
JWT Authentication Filter
    |
    v
Rate Limiting Filter (Redis)
    |
    v
Controller Layer
    |
    v
Service Layer
    |
    v
JPA / MySQL
```


### Architecture Design

- Rate limiting is implemented at the filter layer to intercept abusive traffic before reaching business logic.
- Stateless JWT authentication removes server-side session dependency.
- Layered architecture separates concerns between controller, service, and persistence layers.

---

### Authentication & Security Design

- JWT-based stateless authentication
- Access token validation via custom filter
- BCrypt password hashing
- Authentication principal extraction for user-scoped operations
- Secured API endpoints using Spring Security filter chain

---

## Refresh Token Rotation

To improve authentication security, the system implements **refresh token rotation with conditional database updates**.

Key characteristics:

- Refresh tokens are stored as **SHA-256 hashes** in the database
- Each user maintains **a single refresh token record**
- Every refresh request **rotates the token**
- Previous refresh tokens are **immediately invalidated**

Token rotation is implemented using a conditional update:

UPDATE refresh_token
SET token_hash = :newHash,
    expires_at = :newExp
WHERE member_id = :memberId
AND token_hash = :oldHash

This ensures that **only one refresh request can succeed**, preventing replay attacks and concurrent token reuse.

Security benefits:

- Prevents refresh token replay attacks
- Ensures single active refresh token per user
- Protects against concurrent refresh race conditions

Prevented concurrent refresh replay using conditional database rotation.


## Event Processing & Failure Recovery

To ensure reliability in asynchronous event processing, the system implements idempotent handling and a bounded retry recovery workflow.

### Outbox Pattern & Consistency Guarantee

To ensure consistency between database state and event publication, the system implements the Outbox Pattern with a polling-based publisher.

Instead of directly publishing events after a database write, events are first stored in an `outbox` table within the same transaction. A scheduled poller then reads pending events and publishes them to Redis Streams.

To validate reliability, I simulated a failure scenario by temporarily disabling the outbox poller while generating concurrent requests, allowing events to accumulate in the `PENDING` state. After re-enabling the poller, the system successfully published all pending events.

**Results:**

- Processed 1,000+ concurrent requests with no data loss
- Ensured all pending outbox events were eventually published
- Verified transition from `PENDING → SENT` state for all events
- Prevented inconsistencies between database state and event streams

This experiment demonstrates how the Outbox Pattern guarantees eventual consistency between transactional data and asynchronous event delivery.

### Database Outage Recovery Experiment

To validate resilience under downstream database failures, I simulated a MySQL outage while sending burst traffic to the view-event ingestion API.

During the outage, the API continued accepting events by writing them to Redis Streams instead of depending on immediate database writes. After the database was restored, the consumer resumed processing the backlog and persisted the pending events to MySQL.

**Results:**

- Sent 15,031 requests during the database outage
- Maintained 0% HTTP request failure during the outage
- Sustained p95 latency around 7.3ms while the database was unavailable
- Recovered and persisted queued events after database restoration
- Used idempotent consumers to prevent duplicate writes during replay

This experiment validated that the ingestion path remained available during database failures and that queued events could be recovered after restoration.


### Idempotent Event Processing

- Ensured safe reprocessing under at-least-once delivery semantics
- Designed idempotent operations to prevent duplicate writes and inconsistent state
- Eliminated race conditions in concurrent environments through atomic processing logic

### Failure Recovery Workflow

- Failed events are persisted in a dedicated `failed_events` table
- Each event tracks retry count and processing status (`FAILED`, `RECOVERED`, `DEAD`)
- Implemented manual replay endpoint:
  - `POST /admin/failed-events/{id}/retry`
- Implemented bulk retry endpoint:
  - `POST /admin/failed-events/retry?limit=N`

### Recovery Results

- Simulated 10 failed events in the event pipeline
- Recovered **7 events (70%)** through controlled retries
- Remaining **3 events were marked as DEAD** after exceeding retry thresholds
- Prevented infinite retry loops through bounded retry policies


## Rate Limiting

### Design Goal

To protect the system from excessive traffic and abuse while maintaining predictable performance under concurrent load.

### Implementation Strategy

- Redis-backed request counter
- Atomic increment operations for concurrency safety
- Client IP extraction via `X-Forwarded-For`
- Filtering before controller execution

### Future Enhancements

- Sliding window algorithm
- Token bucket implementation
- Monitoring request metrics

### Rate Limiting Performance

Implemented a Redis-backed rate limiter (60 req/min) using an atomic Lua script to ensure correct TTL behavior under concurrency.

Load tested with k6 (5 VUs, 10s) against `/movies/trending`.

Results:
- Total requests: 2,591
- HTTP 200: 60
- HTTP 429 (throttled): 2,531
- p95 latency: 32.67ms

Under concurrent load, the system maintained sub-33ms p95 response time while predictably throttling excess requests with HTTP 429.

---

## Database Design

- Relational schema modeling using MySQL
- Transactional operations for consistency
- SQL optimization and N+1 problem exploration (planned)

---

## DevOps & Tooling

- Dockerized multi-container setup (App + MySQL + Redis)
- GitHub Actions CI pipeline for automated builds and tests
- Unit and integration testing using JUnit

---

## What I Learned

- How backend systems behave under concurrent load
- Importance of rate limiting and traffic control
- Trade-offs between caching and database consistency
- Designing systems for scalability and reliability

---

## Performance Experiments (Planned)

- Connection pool monitoring
- Intentional N+1 query simulation and resolution
- Cache introduction and latency comparison
- JWT expiration strategy testing
