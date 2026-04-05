package io.github.catimental.diexample.Repository.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.catimental.diexample.domain.event.OutboxEvent;
import java.time.LocalDateTime;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long>{

    List<OutboxEvent> findTop100ByStatusAndRetryAtLessThanEqualOrderByIdAsc(
        String status, 
        LocalDateTime now
    );

}
