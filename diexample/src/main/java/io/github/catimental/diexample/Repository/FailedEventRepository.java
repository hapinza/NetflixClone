package io.github.catimental.diexample.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import io.github.catimental.diexample.domain.event.FailedEvent;
import io.github.catimental.diexample.domain.event.FailedEventStatus;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

public interface FailedEventRepository extends JpaRepository<FailedEvent, Long>{

    List<FailedEvent> findByStatusAndRetryCountLessThanOrderByFailedAtAsc(
        FailedEventStatus status,
        int maxRetryCount,
        Pageable pageable
    );

}
