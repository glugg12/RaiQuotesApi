package com.example.raiquotesv2.controller;

import com.baeldung.openapi.api.QuotesApi;
import com.baeldung.openapi.model.*;
import com.example.raiquotesv2.Exception.*;
import com.example.raiquotesv2.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
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

    public ResponseEntity<QuoteDto> getServerRandomGET(String serverId, String authorId, String authorName){
        try{
            QuoteDto output = quoteService.selectRandomQuote(serverId, authorId, authorName);
            return new ResponseEntity<>(output, HttpStatus.OK);
        }catch(NoQuotesForServerException e){
            throw new EndpointApplicationError(QuoteApiError.NO_QUOTES_FOR_SERVER, e.getMessage());
        }catch(TooManyArgumentsException e){
            throw new EndpointApplicationError(QuoteApiError.TOO_MANY_ARGUMENTS, e.getMessage());
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

    public ResponseEntity<AuthorTotalDto> getAuthorTotalGET(String serverId, String authorId){
        try{
            return new ResponseEntity<>(quoteService.getTotalQuotesAttributedToAuthor(serverId, authorId), HttpStatus.OK);
        }catch(QuoteNotFoundException e){
            throw new EndpointApplicationError(QuoteApiError.QUOTE_NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<RemixQuoteDto> remixQuoteGET(String serverId, @RequestParam(required = false) Integer quoteId, @RequestParam(required = false) String authorId){
        try{
            RemixQuoteDto remixQuoteDto = quoteService.getRemixedQuote(serverId, quoteId, authorId);
            return new ResponseEntity<>(remixQuoteDto, HttpStatus.OK);
        }catch(NoQuotesForServerException e)
        {
            throw new EndpointApplicationError(QuoteApiError.NO_QUOTES_FOR_SERVER, e.getMessage());
        }
        catch(NoSplitDataForQuoteException e)
        {
            throw new EndpointApplicationError(QuoteApiError.NO_SPLIT_DATA_FOR_QUOTE, e.getMessage());
        } catch (QuoteNotFoundException e) {
            throw new EndpointApplicationError(QuoteApiError.QUOTE_NOT_FOUND, e.getMessage());
        } catch (TooManyArgumentsException e) {
            throw new EndpointApplicationError(QuoteApiError.TOO_MANY_ARGUMENTS, e.getMessage());
        }
    }

    public ResponseEntity<ServerQuoteStatsDto> getServerStats(String serverId)
    {
        return new ResponseEntity<>(quoteService.getServerStats(serverId), HttpStatus.OK);
    }

    public ResponseEntity<IndividualQuoteStatsDto> getAuthorStats(String serverId, String authorId){
        return new ResponseEntity<>(quoteService.getAuthorServerStats(serverId,authorId), HttpStatus.OK);
    }

    public ResponseEntity<QuoteSplitDto> getSplits(String serverId, Integer quoteId){
        try{
            return new ResponseEntity<>(quoteService.getSplits(serverId, quoteId), HttpStatus.OK);
        }
        catch(QuoteNotFoundException e)
        {
            throw new EndpointApplicationError(QuoteApiError.QUOTE_NOT_FOUND, e.getMessage());
        }
        catch(NoSplitDataForQuoteException e)
        {
            throw new EndpointApplicationError(QuoteApiError.NO_SPLIT_DATA_FOR_QUOTE, e.getMessage());
        }
    }

    public ResponseEntity<QuoteSplitDto> setSplits(String serverId, Integer quoteId, QuoteSplitRequestDto quoteSplitRequestDto){
        try{
            return new ResponseEntity<>(quoteService.setSplits(serverId,quoteId, quoteSplitRequestDto), HttpStatus.OK);
        }catch(QuoteNotFoundException e)
        {
            throw new EndpointApplicationError(QuoteApiError.QUOTE_NOT_FOUND, e.getMessage());
        }
        catch(NoSplitDataForQuoteException e)
        {
            throw new EndpointApplicationError(QuoteApiError.NO_SPLIT_DATA_FOR_QUOTE, e.getMessage());
        }
    }
}
