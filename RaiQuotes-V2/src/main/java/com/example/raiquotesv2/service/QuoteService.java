package com.example.raiquotesv2.service;

import com.baeldung.openapi.model.*;
import com.example.raiquotesv2.Exception.NoQuotesForServerException;
import com.example.raiquotesv2.Exception.NoSplitDataForQuoteException;
import com.example.raiquotesv2.Exception.QuoteNotFoundException;
import com.example.raiquotesv2.Exception.TooManyArgumentsException;
import com.example.raiquotesv2.entity.Quote;
import com.example.raiquotesv2.entity.RemixSplit;
import com.example.raiquotesv2.repository.QuoteRepository;
import com.example.raiquotesv2.repository.RemixSplitRepository;
import com.example.raiquotesv2.utility.MapperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final RemixSplitRepository remixSplitRepository;

    public QuoteService(QuoteRepository quoteRepository, RemixSplitRepository remixSplitRepository){
        this.quoteRepository = quoteRepository;
        this.remixSplitRepository = remixSplitRepository;
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
        //todo: if there are no quotes in the server, we'll reset to id 1. Is this okay behaviour? Not likely to ever encounter it on gral tho
        Quote quote = MapperService.INSTANCE.AddQuoteRequestDtotoQuote(addQuoteRequestDto);
        quote.setServerQuoteId(getNextQuoteIDForServer(quote.getServerId()));
        quote.setDateAdded(LocalDate.parse(addQuoteRequestDto.getDate()));
        RemixSplit remixSplit = setUpNewQuoteSplits(quote);
        quoteRepository.save(quote);
        remixSplitRepository.save(remixSplit);
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

    public List<QuoteDto> getAllQuotesByServerAndAuthor(String serverId, String authorId) throws NoQuotesForServerException {
        List<QuoteDto> quoteDtos = new ArrayList<>();
        List<Quote> quotes = quoteRepository.findByServerIdAndAuthorId(serverId, authorId);
        if(quotes.isEmpty())
        {
            throw new NoQuotesForServerException("No quotes on server " + serverId + " for author " + authorId);
        }
        quotes.forEach((p)-> quoteDtos.add(MapperService.INSTANCE.quoteToQuoteDto(p)));
        return quoteDtos;
    }

    public List<QuoteDto> getAllQuotesByServerAndAuthorName(String serverId, String authorName) throws NoQuotesForServerException {
        List<QuoteDto> quoteDtos = new ArrayList<>();
        List<Quote> quotes = quoteRepository.findByServerIdAndAuthorName(serverId, authorName);
        if(quotes.isEmpty())
        {
            throw new NoQuotesForServerException("No quotes on server " + serverId + " for author " + authorName);
        }
        quotes.forEach((p)-> quoteDtos.add(MapperService.INSTANCE.quoteToQuoteDto(p)));
        return quoteDtos;
    }

    public QuoteDto selectRandomQuote(String serverId, String authorId, String authorName) throws NoQuotesForServerException, TooManyArgumentsException {
        Random random = new Random();
        List<QuoteDto> quoteDtos = new ArrayList<>();
        if(authorId == null && authorName != null)
        {
            quoteDtos = getAllQuotesByServerAndAuthorName(serverId, authorName);
        }
        else if (authorId != null && authorName == null)
        {
            quoteDtos = getAllQuotesByServerAndAuthor(serverId, authorId);
        }
        else if (authorId != null)
        {
            throw new TooManyArgumentsException("Cannot have both ID and Name (Did you have additional content after a discord mention?)");
        }
        else
        {
            quoteDtos = getAllServerQuotes(serverId);
        }
        int index = random.nextInt(quoteDtos.size());
        return quoteDtos.get(index);
    }

    public QuoteDto getQuoteByServerAndServerQuoteId(String server, Integer quoteId) throws QuoteNotFoundException {
        Optional<Quote> quote = quoteRepository.findByServerIdAndServerQuoteId(server, quoteId);
        return quote.map(MapperService.INSTANCE::quoteToQuoteDto).orElseThrow(()-> new QuoteNotFoundException("No such quote with Id " + quoteId + " in server " + server));
    }

    public void deleteQuoteByByServerAndServerQuoteId(String server, Integer quoteId) throws QuoteNotFoundException {
        Optional<Quote> quote = quoteRepository.findByServerIdAndServerQuoteId(server, quoteId);
        if (quote.isPresent()){
            Optional<RemixSplit> remixSplit = remixSplitRepository.findRemixSplitByQuote(quote.get());
            if (remixSplit.isPresent()){
                remixSplitRepository.delete(remixSplit.get());
                quoteRepository.delete(quote.get());
            }
        }else{
            throw new QuoteNotFoundException("No such quote with Id " + quoteId + " in server " + server);
        }
    }

    public AuthorTotalDto getTotalQuotesAttributedToAuthor(String serverId, String authorId) throws QuoteNotFoundException {
        List<Quote> totalList = quoteRepository.findByAuthorId(authorId);
        if(totalList.isEmpty()){
            throw new QuoteNotFoundException("No such quote in server " + serverId + " for user " + authorId);
        }
        AuthorTotalDto totalDto = new AuthorTotalDto();
        totalDto.setAuthorId(authorId);
        totalDto.setServerId(serverId);
        totalDto.setTimesQuoted(totalList.size());
        return totalDto;
    }

    public RemixQuoteDto getRemixedQuote(String server, Integer quoteId, String authorId) throws NoQuotesForServerException, NoSplitDataForQuoteException, QuoteNotFoundException, TooManyArgumentsException {
        if(quoteId == null)
        {
            if(authorId == null)
            {
                return remix(server);
            }
            else
            {
                return remixWithAuthorId(server, authorId);
            }
        }
        else
        {
            if(authorId == null)
            {
                return remixWithQuoteId(server, quoteId);
            }
            else
            {
                throw new TooManyArgumentsException("Cannot have both quoteId and authorId");
            }
        }
    }

    public ServerQuoteStatsDto getServerStats(String serverId){
        ServerQuoteStatsDto serverQuoteStatsDto = new ServerQuoteStatsDto();
        List<Quote> quotes = quoteRepository.findByServerId(serverId);
        serverQuoteStatsDto.setTotalQuotes(quotes.size());
        serverQuoteStatsDto.setServerId(serverId);
        return serverQuoteStatsDto;
    }

    public IndividualQuoteStatsDto getAuthorServerStats(String serverId, String authorId){
        IndividualQuoteStatsDto individualQuoteStatsDto = new IndividualQuoteStatsDto();
        List<Quote> quotes = quoteRepository.findByServerIdAndAuthorId(serverId, authorId);
        List<Quote> addedQuotes = quoteRepository.findByServerIdAndAddedBy(serverId, authorId);
        individualQuoteStatsDto.setAuthorId(authorId);
        individualQuoteStatsDto.setTotalQuotesAdded(addedQuotes.size());
        individualQuoteStatsDto.setTotalTimesQuoted(quotes.size());
        individualQuoteStatsDto.setServerId(serverId);
        return individualQuoteStatsDto;
    }

    //private functions
    private RemixSplit setUpNewQuoteSplits(Quote quote){
        RemixSplit split = new RemixSplit();
        split.setQuote(quote);
        int findRightPos = quote.getQuote().lastIndexOf(" ", Math.round(((float) quote.getQuote().length() /2)));
        int findLeftPos = quote.getQuote().indexOf(" ", (quote.getQuote().length() /2));
        if (findLeftPos == -1){
            findLeftPos = quote.getQuote().length();
        }
        if(findRightPos == -1){
            findRightPos = 0;
        }
        split.setSplitLeftEndPos(findLeftPos);
        split.setSplitRightEndPos(findRightPos);
        return split;
    }

    private int getNextQuoteIDForServer(String server){
        List<Quote> quotes =  quoteRepository.findByServerId(server);
        AtomicInteger nextId = new AtomicInteger();
        quotes.forEach((p)-> nextId.set(Math.max(p.getServerQuoteId(), nextId.get())));
        return nextId.get() + 1;
    }

    private RemixQuoteDto remix(String server) throws NoQuotesForServerException, NoSplitDataForQuoteException {
        List<QuoteDto> quoteDtos = getAllServerQuotes(server);
        Random random = new Random();
        int firstId = random.nextInt(quoteDtos.size());
        int secondId = random.nextInt(quoteDtos.size());
        Quote firstQuote = MapperService.INSTANCE.quoteDtoToQuote(quoteDtos.get(firstId));
        Quote secondQuote = MapperService.INSTANCE.quoteDtoToQuote(quoteDtos.get(secondId));
        return executeRemix(firstQuote, secondQuote);
    }

    private RemixQuoteDto executeRemix(Quote firstQuote, Quote secondQuote) throws NoSplitDataForQuoteException {
        int firstEnd = remixSplitRepository.findRemixSplitByQuote(firstQuote).map(RemixSplit::getSplitLeftEndPos).orElseThrow(() -> new NoSplitDataForQuoteException("No split data for quote " + firstQuote.getId()));
        int secondEnd = remixSplitRepository.findRemixSplitByQuote(secondQuote).map(RemixSplit::getSplitRightEndPos).orElseThrow(() -> new NoSplitDataForQuoteException("No split data for quote " + secondQuote.getId()));
        String remixed = StringUtils.substring(firstQuote.getQuote(), 0, firstEnd) + StringUtils.substring(secondQuote.getQuote(), secondEnd, secondQuote.getQuote().length());
        RemixQuoteDto remixQuoteDto = new RemixQuoteDto();
        remixQuoteDto.setQuote(remixed);
        remixQuoteDto.setAuthor1(firstQuote.getAuthorId());
        remixQuoteDto.setQuoteId1(firstQuote.getServerQuoteId());
        remixQuoteDto.setAuthor2(secondQuote.getAuthorId());
        remixQuoteDto.setQuoteId2(secondQuote.getServerQuoteId());
        return remixQuoteDto;
    }

    private RemixQuoteDto remixWithAuthorId(String server, String authorId) throws NoQuotesForServerException, NoSplitDataForQuoteException {
        List<QuoteDto> quoteDtos = getAllQuotesByServerAndAuthor(server, authorId);
        Random random = new Random();
        int firstId = random.nextInt(quoteDtos.size());
        int secondId = random.nextInt(quoteDtos.size());
        Quote firstQuote = MapperService.INSTANCE.quoteDtoToQuote(quoteDtos.get(firstId));
        Quote secondQuote = MapperService.INSTANCE.quoteDtoToQuote(quoteDtos.get(secondId));
        return executeRemix(firstQuote, secondQuote);
    }

    private RemixQuoteDto remixWithQuoteId(String server, int quoteId) throws NoQuotesForServerException, QuoteNotFoundException, NoSplitDataForQuoteException {
        List<QuoteDto> quoteDtos = getAllServerQuotes(server);
        Random random = new Random();
        int leftOrRight = random.nextInt(2);
        Quote firstQuote;
        Quote secondQuote;
        if(leftOrRight == 0)
        {
            int secondId = random.nextInt(quoteDtos.size());
            firstQuote = MapperService.INSTANCE.quoteDtoToQuote(getQuoteByServerAndServerQuoteId(server, quoteId));
            secondQuote = MapperService.INSTANCE.quoteDtoToQuote(quoteDtos.get(secondId));
        }
        else
        {
            int firstId = random.nextInt(quoteDtos.size());
            firstQuote = MapperService.INSTANCE.quoteDtoToQuote(quoteDtos.get(firstId));
            secondQuote = MapperService.INSTANCE.quoteDtoToQuote(getQuoteByServerAndServerQuoteId(server, quoteId));
        }
        return executeRemix(firstQuote, secondQuote);
    }
}
