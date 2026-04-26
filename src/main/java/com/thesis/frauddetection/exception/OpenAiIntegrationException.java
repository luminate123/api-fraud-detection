package com.thesis.frauddetection.exception;

public class OpenAiIntegrationException extends RuntimeException {
    public OpenAiIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
