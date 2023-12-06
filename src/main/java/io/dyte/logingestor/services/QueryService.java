package io.dyte.logingestor.services;

import io.dyte.logingestor.model.ApiResponse;
import io.dyte.logingestor.model.Log;
import io.dyte.logingestor.model.QueryPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QueryService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public ApiResponse<List<Log>> saveLog(Log log, String requestId) {
        try {
            mongoTemplate.save(log);
            return new ApiResponse<>(HttpStatus.CREATED.value(), new ArrayList<>(), "Success", requestId);
        } catch (DataAccessException ex) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), new ArrayList<>(), "Error saving log", requestId);
        }
    }

    public ApiResponse<List<Log>> buildAndExecuteQuery(QueryPayload payload, String requestId) {
        try {
            Criteria criteria = new Criteria();

            if (payload.getLevel() != null) {
                criteria.and("level").is(payload.getLevel());
            }

            if (payload.getMessage() != null) {
                criteria.and("message").regex(payload.getMessage(), "i");
            }

            if (payload.getResourceId() != null) {
                criteria.and("resourceId").regex(payload.getResourceId());
            }

            if (payload.getTimestamp() != null) {
                criteria.and("timestamp").is(payload.getTimestamp());
            }

            if (payload.getStartTimestamp() != null && payload.getEndTimestamp() != null) {
                criteria.and("timestamp").gte(payload.getStartTimestamp()).lte(payload.getEndTimestamp());
            } else if (payload.getStartTimestamp() != null) {
                criteria.and("timestamp").gte(payload.getStartTimestamp());
            } else if (payload.getEndTimestamp() != null) {
                criteria.and("timestamp").lte(payload.getEndTimestamp());
            }

            if (payload.getTraceId() != null) {
                criteria.and("traceId").regex(payload.getTraceId());
            }

            if (payload.getSpanId() != null) {
                criteria.and("spanId").regex(payload.getSpanId());
            }

            if (payload.getCommit() != null) {
                criteria.and("commit").regex(payload.getCommit());
            }

            if (payload.getParentResourceId() != null) {
                criteria.and("metadata.parentResourceId").regex(payload.getParentResourceId());
            }

            Query query = new Query(criteria);
            query.fields().exclude("_id");
            System.out.println("RequestId: " + requestId + " | " + query);
            List<Log> logs = mongoTemplate.find(query, Log.class);
            return new ApiResponse<>(HttpStatus.OK.value(), logs, "Success", requestId);
        } catch (Exception e) {
            System.out.println("RequestId: " + requestId + " | Error: " + e.toString());
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), new ArrayList<>(), "Failed", requestId);
        }
    }
}