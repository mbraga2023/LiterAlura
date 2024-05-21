package com.alura.LiterAlura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookDto(
        long id,
        String title,
        List<AuthorDto> authors,
        List<String> languages,
        int download_count


) {
    @Override
    public String toString() {
        String authorsString = authors.stream()
                .map(author -> author.name() + " (" + author.birth_year() + "-" + author.death_year() + ")")
                .collect(Collectors.joining(", "));

        String languagesString = String.join(", ", languages);

        return "Livro ID: " + id + "\n" +
                "Título: " + title + "\n" +
                "Autores: " + authorsString + "\n" +
                "Idiomas: " + languagesString + "\n" +
                "Número de Downloads: " + download_count + "\n";
    }
}
