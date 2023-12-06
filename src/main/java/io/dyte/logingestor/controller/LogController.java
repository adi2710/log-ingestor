package io.dyte.logingestor.controller;

import io.dyte.logingestor.model.ApiResponse;
import io.dyte.logingestor.model.Log;
import io.dyte.logingestor.model.QueryPayload;
import io.dyte.logingestor.services.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
public class LogController {
    @Autowired
    private QueryService queryService;

    @PostMapping("/ingest")
    public ResponseEntity<ApiResponse<List<Log>>> ingestLog(@Valid @RequestBody Log log) {
        String requestId = UUID.randomUUID().toString();
        ApiResponse<List<Log>> response = queryService.saveLog(log, requestId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/query")
    public ResponseEntity<ApiResponse<List<Log>>> queryLogs(@Valid @RequestBody QueryPayload payload) {
        String requestId = UUID.randomUUID().toString();

        System.out.println("RequestId: " + requestId + " | " + payload.toString());
        ApiResponse<List<Log>> response = null;

        Instant timestamp = payload.getTimestamp();
        Instant startTimestamp = payload.getStartTimestamp();
        Instant endTimestamp = payload.getEndTimestamp();
        if (timestamp != null && (startTimestamp != null || endTimestamp != null)) {
            response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), new ArrayList<>(), "Invalid parameters: Either provide 'timestamp' or 'startTimestamp' and 'endTimestamp', not both.", requestId);
        } else {
            response = queryService.buildAndExecuteQuery(payload, requestId);
        }
        return ResponseEntity.status(response.getCode()).body(response);
    }
}