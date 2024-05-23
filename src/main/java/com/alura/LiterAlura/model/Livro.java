package com.alura.LiterAlura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    private Long id;
    private String title;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "livro_autor",
            joinColumns = @JoinColumn(name = "livro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> autores = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> languages;

    private int download_count;

    public Livro() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public int getDownload_count() {
        return download_count;
    }

    public void setDownload_count(int download_count) {
        this.download_count = download_count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("")
                .append("id:").append(id)
                .append(", Título: ").append(title);

        // Print details of each Autor
        sb.append(", Autores: ");
        for (Autor autor : autores) {
            sb.append("").append(autor.getName())
                    .append(" - Nascimento: ").append(autor.getBirthYear())
                    .append(", Falecimento: ").append(autor.getDeathYear())
                    .append("; ");
        }
        sb.append("");

        // Print languages and download count
        sb.append("Idiomas:").append(languages)
                .append(", Nº downloads:").append(download_count);

        return sb.toString();
    }

}
