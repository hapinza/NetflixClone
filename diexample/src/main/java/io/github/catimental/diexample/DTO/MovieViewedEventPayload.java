package io.github.catimental.diexample.DTO;

public record MovieViewedEventPayload (
    String eventId,
    String memberId,
    String movieId,
    String occuredAt
){}
