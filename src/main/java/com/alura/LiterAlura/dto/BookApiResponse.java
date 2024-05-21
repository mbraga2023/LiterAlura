package com.alura.LiterAlura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookApiResponse {
    private List<BookDto> results;

    public BookApiResponse() {
    }

    public List<BookDto> getResults() {
        return results;
    }

    public void setResults(List<BookDto> results) {
        this.results = results;
    }
}