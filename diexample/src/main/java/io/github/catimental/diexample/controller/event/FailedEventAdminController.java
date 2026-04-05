package io.github.catimental.diexample.controller.event;

import io.github.catimental.diexample.Service.FailedEventRetryService;
import io.micrometer.core.ipc.http.HttpSender.Response;
import lombok.RequiredArgsConstructor;
import io.github.catimental.diexample.DTO.BulkRetryResponse;
import io.github.catimental.diexample.DTO.RetryResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/failed-events")
public class FailedEventAdminController  {
    private final FailedEventRetryService failedEventRetryService;

    // one
    @PostMapping("/{id}/retry")
    public ResponseEntity<RetryResult> retryOne(@PathVariable Long id){
        return ResponseEntity.ok(failedEventRetryService.retryOne(id));
    }


    //many
    @PostMapping("/retry")
    public ResponseEntity<BulkRetryResponse> retryFailed(
        @RequestParam(defaultValue = "10") int limit
    ){
        return ResponseEntity.ok(failedEventRetryService.retryFailed(limit));
    }


}   
