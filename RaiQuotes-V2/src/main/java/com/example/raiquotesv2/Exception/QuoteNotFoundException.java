package com.example.raiquotesv2.Exception;

public class QuoteNotFoundException extends Exception {
    public QuoteNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
