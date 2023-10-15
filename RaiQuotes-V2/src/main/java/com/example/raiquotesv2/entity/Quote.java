package com.example.raiquotesv2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer Id;

    private String serverId;

    private Integer serverQuoteId;

    private LocalDateTime dateAdded;

    private String addedBy;

    private String authorId;

    private String authorName;

    private String quote;

    private String imageUrl;
}
