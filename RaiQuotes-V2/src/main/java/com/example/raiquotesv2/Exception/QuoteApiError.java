package com.example.raiquotesv2.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum QuoteApiError {
    QUOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "Quote not found"),
    NO_QUOTES_FOR_SERVER(HttpStatus.PRECONDITION_FAILED, "No quotes for this server ID");
    private final HttpStatus status;

    private final String message;
    QuoteApiError(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }
}
