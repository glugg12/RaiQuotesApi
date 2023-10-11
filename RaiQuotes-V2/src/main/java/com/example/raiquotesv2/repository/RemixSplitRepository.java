package com.example.raiquotesv2.repository;

import com.example.raiquotesv2.entity.Quote;
import com.example.raiquotesv2.entity.RemixSplit;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RemixSplitRepository extends CrudRepository<RemixSplit, Integer> {
    Optional<RemixSplit> findRemixSplitByQuote(Quote quote);
}
