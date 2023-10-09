package com.example.raiquotesv2.Handler;

import com.baeldung.openapi.model.ApiError;
import com.example.raiquotesv2.Exception.EndpointApplicationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class EndpointApplicationErrorHandler {
    @ExceptionHandler({EndpointApplicationError.class})
    public ResponseEntity<ApiError> handleEndpointApplicationError(EndpointApplicationError error){
        log.info("Handling error: {}", error.getMessage());
        ApiError apiError = new ApiError();
        apiError.setTitle(error.getError().name());
        apiError.setMessage(error.getMessage());
        apiError.setDetail(error.getVerboseMessage() == null? error.getMessage() : error.getVerboseMessage());
        return new ResponseEntity<>(apiError, error.getError().getStatus());
    }
}
