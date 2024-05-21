package com.alura.LiterAlura.service;

import com.alura.LiterAlura.dto.AuthorDto;
import com.alura.LiterAlura.dto.BookApiResponse;
import com.alura.LiterAlura.dto.BookDto;
import com.alura.LiterAlura.model.Autor;
import com.alura.LiterAlura.model.Livro;
import com.alura.LiterAlura.repository.AutorRepository;
import com.alura.LiterAlura.repository.LivroRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private String apiUrl;

    @PostConstruct
    public void initialize() {
        apiUrl = "http://gutendex.com/books?search=";
    }

    @Autowired
    private ConsumoApi consumoApi;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void buscaPorTituloDoLivro() {
        search("Enter book title: ", "title");
    }

    public void buscaPorAutor() {
      //searchAndSave("Enter author name: ", "author");
    }

   /* private void search(String prompt, String parameter) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        String userInput = scanner.nextLine();
        String url = apiUrl + userInput.replace(" ", "+");
        String response = consumoApi.obterDados(url);

        Conversor converter = new Conversor();
        try {
            BookApiResponse bookApiResponse = converter.obterDados(response, BookApiResponse.class);
            List<BookDto> books = bookApiResponse.getResults();

            // Print book details
            for (BookDto book : books) {
                System.out.println("Livro ID: " + book.id());
                System.out.println("Título: " + book.title());

                // Convert authors list to string without square brackets
                String authorsString = book.authors().stream()
                        .map(author -> author.name() + " (" + author.birth_year() + "-" + author.death_year() + ")")
                        .collect(Collectors.joining(", "));
                System.out.println("Autores: " + authorsString);

                // Convert languages list to string without square brackets
                String languagesString = String.join(", ", book.languages());
                System.out.println("Idiomas: " + languagesString);

                System.out.println("Número de Downloads: " + book.download_count());
                System.out.println();
            }
        } catch (RuntimeException e) {
            System.out.println("Erro ao processar resposta JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }*/

    private void search(String prompt, String parameter) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        String userInput = scanner.nextLine();

        String url = apiUrl + userInput.replace(" ", "+");
        String response = consumoApi.obterDados(url);

        Conversor converter = new Conversor();
        try {
            BookApiResponse bookApiResponse = converter.obterDados(response, BookApiResponse.class);
            List<BookDto> books = bookApiResponse.getResults();

            // Check if the response JSON is empty
            if (books.isEmpty()) {
                System.out.println("Nenhum livro encontrado para a pesquisa: " + userInput);
                return;
            }

            // Print book details using toString method of BookDto
            for (BookDto book : books) {
                System.out.println(book); // This will invoke the toString method
                System.out.println(); // Add a newline after each book
            }

            // Save the books and authors to the database
            saveToDatabase(books);
        } catch (RuntimeException e) {
            System.out.println("Erro ao processar resposta JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveToDatabase(List<BookDto> books) {
        for (BookDto bookDto : books) {
            // Create or update livro
            Livro livro = new Livro();
            livro.setId(bookDto.id());
            livro.setTitle(bookDto.title());
            livro.setLanguages(bookDto.languages());
            livro.setDownload_count(bookDto.download_count());
            livro = livroRepository.save(livro); // Save livro and get the managed entity

            // Iterate through authors
            for (AuthorDto authorDto : bookDto.authors()) {
                // Check if author already exists
                Optional<Autor> existingAutor = autorRepository.findByName(authorDto.name());

                Autor autor;
                if (existingAutor.isPresent()) {
                    autor = existingAutor.get();
                } else {
                    // Create new author
                    autor = new Autor();
                    autor.setName(authorDto.name());
                    autor.setBirthYear(authorDto.birth_year());
                    autor.setDeathYear(authorDto.death_year());
                    autor = autorRepository.save(autor); // Save autor and get the managed entity
                }

                // Add livro to the autor's livros list
                if (!autor.getLivros().contains(livro)) {
                    autor.getLivros().add(livro);
                    autorRepository.save(autor); // Update autor
                }

                // Add autor to the livro's autores list
                if (!livro.getAutores().contains(autor)) {
                    livro.getAutores().add(autor);
                    livroRepository.save(livro); // Update livro
                }
            }
        }

        System.out.println("Livros salvos com sucesso!");
    }



}




