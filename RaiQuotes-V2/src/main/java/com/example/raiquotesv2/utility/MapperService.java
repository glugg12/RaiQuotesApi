package com.example.raiquotesv2.utility;

import com.baeldung.openapi.model.AddQuoteRequestDto;
import com.baeldung.openapi.model.QuoteDto;
import com.example.raiquotesv2.entity.Quote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MapperService {

    MapperService INSTANCE = Mappers.getMapper(MapperService.class);
    QuoteDto quoteToQuoteDto(Quote quote);
    Quote quoteDtoToQuote(QuoteDto quoteDto);

    @Mapping(target = "addedBy", source = "addedBy")
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "authorName", source = "authorName")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "serverId", source = "serverId")
    Quote AddQuoteRequestDtotoQuote(AddQuoteRequestDto addQuoteRequestDto);

}
