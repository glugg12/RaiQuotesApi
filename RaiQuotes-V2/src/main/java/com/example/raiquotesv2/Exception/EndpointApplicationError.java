package com.example.raiquotesv2.Exception;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class EndpointApplicationError extends RuntimeException{

    private QuoteApiError error;
    private String verboseMessage;
    public EndpointApplicationError(QuoteApiError exception){
        super(exception.getMessage());
        this.error = exception;
    }

    public EndpointApplicationError(QuoteApiError exception, String detail){
        super(exception.getMessage());
        this.error = exception;
        this.verboseMessage = detail;
    }
}
