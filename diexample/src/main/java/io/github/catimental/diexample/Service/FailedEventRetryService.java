


       package io.github.catimental.diexample.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import io.github.catimental.diexample.Consumer.Redis.MovieViewedConsumer;
import io.github.catimental.diexample.DTO.trending.MovieViewedEvent;
import io.github.catimental.diexample.Repository.FailedEventRepository;
import io.github.catimental.diexample.Repository.ProcessedEventRepository;
import io.github.catimental.diexample.domain.event.FailedEvent;
import io.github.catimental.diexample.domain.event.FailedEventStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import io.github.catimental.diexample.domain.event.ProcessedEvent;
import io.github.catimental.diexample.DTO.MovieViewedEventPayload;
import io.github.catimental.diexample.DTO.RetryResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.data.domain.Pageable;
import io.github.catimental.diexample.DTO.BulkRetryResponse;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class FailedEventRetryService{
    
    private static final  int MAX_RETRY_COUNT = 3;
    

    private final FailedEventRepository failedEventRepository;
    private final ViewAggregationService viewAggregationService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;



    @Transactional
    public RetryResult retryOne(Long failedEventId){
        FailedEvent failedEvent = failedEventRepository.findById(failedEventId)
                                    .orElseThrow(() -> new IllegalArgumentException("fail event not found"));

        if(failedEvent.getStatus() == FailedEventStatus.RECOVERED){
            return new RetryResult(false, "already recovered");
        }

        if(failedEvent.getRetryCount() >= MAX_RETRY_COUNT){
            failedEvent.setStatus(FailedEventStatus.DEAD);
            return new RetryResult(false, "max retry exceeded");
        }

        try{
            MovieViewedEventPayload payload = objectMapper.readValue(
                failedEvent.getPayload(),
                MovieViewedEventPayload.class
            );
            String memberRaw = payload.memberId();
            Long memberId = (memberRaw == null || memberRaw.isBlank()) ? null : Long.parseLong(memberRaw); 
            Long movieId = Long.parseLong(payload.movieId());

            viewAggregationService.processedViewEvent(
                payload.eventId(), 
                memberId,
                movieId
            );




            failedEvent.setStatus(FailedEventStatus.RECOVERED);
            failedEvent.makeRecovered();
            failedEvent.makeRetried();

            
            processedEventRepository.save(
                new ProcessedEvent(payload.eventId())
            );

            return new RetryResult(true, "recovered");
        }catch (NumberFormatException | JsonProcessingException e) {
        failedEvent.makeRetried();
        failedEvent.setStatus(FailedEventStatus.DEAD);

        return new RetryResult(false, "invalid payload");

    } catch (Exception e) {
        failedEvent.makeRetried();

        if (failedEvent.getRetryCount() >= MAX_RETRY_COUNT) {
            failedEvent.setStatus(FailedEventStatus.DEAD);
        }

        return new RetryResult(false, e.getMessage());
    }
}

@Transactional
public BulkRetryResponse retryFailed(int limit){
    List<FailedEvent> failedEvents = failedEventRepository.findByStatusAndRetryCountLessThanOrderByFailedAtAsc(
        FailedEventStatus.FAILED, 
        MAX_RETRY_COUNT, 
        PageRequest.of(0, limit));

        List<RetryResult> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for(FailedEvent failedEvent : failedEvents){
            RetryResult result = retryOne(failedEvent.getId());
            results.add(result);

         if(result.success()){
            successCount++;
         }else{
            failureCount++;
         }
        }

        return new BulkRetryResponse(
            limit,
            failedEvents.size(),
            successCount,
            failureCount,
            results
        );

    
}


}
