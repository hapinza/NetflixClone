package io.github.catimental.diexample.publisher;
import io.github.catimental.diexample.DTO.trending.MovieViewedEvent;

import java.util.Map;

public interface MovieEventPublisher {
    void publishMovieViewed(MovieViewedEvent event);

}
