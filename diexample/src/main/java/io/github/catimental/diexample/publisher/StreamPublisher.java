package io.github.catimental.diexample.publisher;

import java.util.Map;

public interface StreamPublisher {
    
    void publish(String streamKey, Map<String, String> payload);
}
