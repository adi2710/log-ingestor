package io.dyte.logingestor.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
public class QueryPayload {
    private String level;
    private String message;
    private String resourceId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant timestamp;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant startTimestamp;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant endTimestamp;
    private String traceId;
    private String spanId;
    private String commit;
    private String parentResourceId;
}
