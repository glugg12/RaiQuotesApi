package com.example.raiquotesv2.service;

import com.baeldung.openapi.model.AddQuoteRequestDto;
import com.baeldung.openapi.model.QuoteDto;
import com.example.raiquotesv2.Exception.NoQuotesForServerException;
import com.example.raiquotesv2.Exception.QuoteNotFoundException;
import com.example.raiquotesv2.entity.Quote;
import com.example.raiquotesv2.repository.QuoteRepository;
import com.example.raiquotesv2.utility.MapperService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class QuoteService {

    private final QuoteRepository quoteRepository;

    public QuoteService(QuoteRepository quoteRepository){
        this.quoteRepository = quoteRepository;
    }

    public List<QuoteDto> getAllQuotes(){
        List<QuoteDto> quoteDtos = new ArrayList<>();
        Iterable<Quote> quotes = quoteRepository.findAll();
        quotes.forEach((p) -> quoteDtos.add(MapperService.INSTANCE.quoteToQuoteDto(p)));
        return quoteDtos;
    }

    public QuoteDto saveQuote(AddQuoteRequestDto addQuoteRequestDto){
        //convert dto to quote
        //build server quote id based on how many of server id quotes exist bc jpa sequence not possible??
        //save quote
        Quote quote = MapperService.INSTANCE.AddQuoteRequestDtotoQuote(addQuoteRequestDto);
        List<Quote> quotesFromServer = quoteRepository.findByServerId(addQuoteRequestDto.getServerId());
        quote.setServerQuoteId(quotesFromServer.size() + 1);
        quote.setDateAdded(LocalDate.parse(addQuoteRequestDto.getDate()));
        quoteRepository.save(quote);
        return MapperService.INSTANCE.quoteToQuoteDto(quote);
    }

    public QuoteDto getQuoteWithId(Integer Id) throws QuoteNotFoundException {
        Optional<Quote> quote = quoteRepository.findById(Id);
        return quote.map(MapperService.INSTANCE::quoteToQuoteDto).orElseThrow(()-> new QuoteNotFoundException("No such quote with Id " + Id));
    }

    public List<QuoteDto> getAllServerQuotes(String serverId) throws NoQuotesForServerException {
        List<QuoteDto> quoteDtos = new ArrayList<>();
        List<Quote> quotes = quoteRepository.findByServerId(serverId);
        if(quotes.isEmpty())
        {
            throw new NoQuotesForServerException("No quotes belong to server " + serverId);
        }
        quotes.forEach((p)-> quoteDtos.add(MapperService.INSTANCE.quoteToQuoteDto(p)));
        return quoteDtos;
    }

    public QuoteDto selectRandomQuote(List<QuoteDto> quoteDtoList){
        Random random = new Random();
        int index = random.nextInt(quoteDtoList.size());
        return quoteDtoList.get(index);
    }

    public QuoteDto getQuoteByServerAndServerQuoteId(String server, Integer quoteId) throws QuoteNotFoundException {
        Optional<Quote> quote = quoteRepository.findByServerIdAndServerQuoteId(server, quoteId);
        return quote.map(MapperService.INSTANCE::quoteToQuoteDto).orElseThrow(()-> new QuoteNotFoundException("No such quote with Id " + quoteId + " in server " + server));
    }

    public void deleteQuoteByByServerAndServerQuoteId(String server, Integer quoteId) throws QuoteNotFoundException {
        Optional<Quote> quote = quoteRepository.findByServerIdAndServerQuoteId(server, quoteId);
        if (quote.isPresent()){
            quoteRepository.delete(quote.get());
        }else{
            throw new QuoteNotFoundException("No such quote with Id " + quoteId + " in server " + server);
        }
    }
}
