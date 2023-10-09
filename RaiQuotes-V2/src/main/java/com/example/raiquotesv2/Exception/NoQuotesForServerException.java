package com.example.raiquotesv2.Exception;

public class NoQuotesForServerException extends Exception{
    public NoQuotesForServerException(String errorMessage){
        super(errorMessage);
    }
}
