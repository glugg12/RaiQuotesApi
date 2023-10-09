package com.example.raiquotesv2.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum QuoteApiError {
    QUOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "Quote not found");
    private final HttpStatus status;

    private final String message;
    QuoteApiError(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }
}
