package com.example.raiquotesv2.service;

import com.baeldung.openapi.model.AddQuoteRequestDto;
import com.baeldung.openapi.model.QuoteDto;
import com.example.raiquotesv2.entity.Quote;
import com.example.raiquotesv2.repository.QuoteRepository;
import com.example.raiquotesv2.utility.MapperService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
}
