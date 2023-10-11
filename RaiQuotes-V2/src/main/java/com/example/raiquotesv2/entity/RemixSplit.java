package com.example.raiquotesv2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class RemixSplit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer Id;
    @OneToOne
    private Quote quote;

    private Integer splitLeftEndPos;

    private Integer splitRightEndPos;
}
