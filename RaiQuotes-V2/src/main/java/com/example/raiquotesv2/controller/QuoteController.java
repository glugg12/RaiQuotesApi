package com.example.raiquotesv2.controller;

import com.baeldung.openapi.api.QuotesApi;
import com.baeldung.openapi.model.AddQuoteRequestDto;
import com.baeldung.openapi.model.QuoteDto;
import com.example.raiquotesv2.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QuoteController implements QuotesApi {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService){
        this.quoteService = quoteService;
    }

    public ResponseEntity<List<QuoteDto>> getAllQuotesGET(){
        return new ResponseEntity<>(quoteService.getAllQuotes(), HttpStatus.OK);
    }

    public ResponseEntity<QuoteDto> addQuotePOST(AddQuoteRequestDto addQuoteRequestDto){
        return new ResponseEntity<>(quoteService.saveQuote(addQuoteRequestDto), HttpStatus.OK);
    }
}
