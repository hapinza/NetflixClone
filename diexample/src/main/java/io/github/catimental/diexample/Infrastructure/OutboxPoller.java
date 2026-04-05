package io.github.catimental.diexample.Infrastructure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import io.github.catimental.diexample.Repository.outbox.OutboxRepository;
import io.github.catimental.diexample.domain.event.OutboxEvent;
import io.github.catimental.diexample.publisher.RedisStreamPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.Map;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDateTime;
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPoller {
    
    private final OutboxRepository outboxRepository;
    private final RedisStreamPublisher redisPublisher;
    private final ObjectMapper objectMapper;

    private static final String STREAM_KEY = "like-events";


    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishPendingEvents(){
        List<OutboxEvent> events =
           outboxRepository.findTop100ByStatusAndRetryAtLessThanEqualOrderByIdAsc(
            "PENDING",
            LocalDateTime.now()
        );


        for(OutboxEvent event : events){
            try{
                Map<String, String> payload = objectMapper.readValue(
                    event.getPayload(),
                    new TypeReference<Map<String,String>>(){}
                );

                redisPublisher.publish(STREAM_KEY, payload);
                event.markSent();
            }catch(Exception e){
                log.warn("Failed to publish outbox event. evnetId = {}, error ={}",
                event.getEventId(), event.getMessage(), e);
                
                if(event.getRetryCount() > 3){
                    event.markFailed(e.getMessage());

                }else{
                    // could be updated under conditions
                    LocalDateTime nextRetry = LocalDateTime.now()
                                                .plusSeconds((long)Math.pow(2, event.getRetryCount()+1));
                    event.markRetry(e.getMessage(), nextRetry);                            
                }

            }



        }




    }




}
