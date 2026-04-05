package io.github.catimental.diexample.DTO;
import java.util.List;
import io.github.catimental.diexample.DTO.RetryResult;


public record BulkRetryResponse (
    int requrestlimit, 
    int processedCount,
    int successCount,
    int failureCount,
    List<RetryResult> results
){
    
}
