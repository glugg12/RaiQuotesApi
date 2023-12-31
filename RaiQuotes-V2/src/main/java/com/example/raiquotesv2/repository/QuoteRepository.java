package com.example.raiquotesv2.repository;

import com.example.raiquotesv2.entity.Quote;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface QuoteRepository extends CrudRepository<Quote, Integer> {
    List<Quote> findByServerId(String serverId);
    List<Quote> findByAuthorId(String authorId);
    Optional<Quote> findByServerIdAndServerQuoteId(String serverId, Integer quoteId);
    List<Quote> findByServerIdAndAuthorId(String serverId, String authorId);

    List<Quote> findByServerIdAndAuthorName(String serverId, String authorName);
    List<Quote> findByServerIdAndAddedBy(String serverId, String addedBy);
}
