package io.dyte.logingestor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.Map;

@Data
@Document(collection = "log_data")
public class Log {
    @Id
    private String id;
    private String level;
    private String message;
    private String resourceId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant timestamp;
    private String traceId;
    private String spanId;
    private String commit;
    private Map<String, String> metadata;

    @JsonIgnore
    public String getId() {
        return id;
    }
}