package com.thesis.frauddetection.dto;

import java.time.Instant;
import java.util.List;

public class ErrorResponseDto {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private List<String> details;

    public ErrorResponseDto(Instant timestamp, int status, String error, String message, List<String> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getDetails() {
        return details;
    }
}
