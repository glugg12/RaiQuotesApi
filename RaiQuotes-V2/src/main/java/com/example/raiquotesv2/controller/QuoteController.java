package com.example.raiquotesv2.controller;

import com.baeldung.openapi.api.QuotesApi;
import com.baeldung.openapi.model.AddQuoteRequestDto;
import com.baeldung.openapi.model.QuoteDto;
import com.example.raiquotesv2.Exception.EndpointApplicationError;
import com.example.raiquotesv2.Exception.NoQuotesForServerException;
import com.example.raiquotesv2.Exception.QuoteApiError;
import com.example.raiquotesv2.Exception.QuoteNotFoundException;
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

    public ResponseEntity<QuoteDto> getQuoteGET(Integer Id)
    {
        try{
            return new ResponseEntity<>(quoteService.getQuoteWithId(Id), HttpStatus.OK);
        }
        catch(QuoteNotFoundException e){
            throw new EndpointApplicationError(QuoteApiError.QUOTE_NOT_FOUND);
        }
    }

    public ResponseEntity<QuoteDto> getServerRandomGET(String serverId){
        try{
            QuoteDto output = quoteService.selectRandomQuote(quoteService.getAllServerQuotes(serverId));
            return new ResponseEntity<>(output, HttpStatus.OK);
        }catch(NoQuotesForServerException e){
            throw new EndpointApplicationError(QuoteApiError.NO_QUOTES_FOR_SERVER, e.getMessage());
        }
    }

    public ResponseEntity<QuoteDto> getQuoteFromServerGET(String serverId, Integer id){
        try{
            return new ResponseEntity<>(quoteService.getQuoteByServerAndServerQuoteId(serverId, id), HttpStatus.OK);
        }
        catch (QuoteNotFoundException e){
            throw new EndpointApplicationError(QuoteApiError.QUOTE_NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<Void> deleteQuoteByServerQuoteIdDELETE(String serverId, Integer id){
        try{
            quoteService.deleteQuoteByByServerAndServerQuoteId(serverId, id);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(QuoteNotFoundException e){
            throw new EndpointApplicationError(QuoteApiError.QUOTE_NOT_FOUND, e.getMessage());
        }
    }
}
