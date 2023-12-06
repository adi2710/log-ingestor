package io.dyte.logingestor.model;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private final Integer code;
    private final T data;
    private final String message;
    private final String requestId;
}