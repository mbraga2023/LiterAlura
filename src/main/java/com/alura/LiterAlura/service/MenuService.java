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

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

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
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o título do livro: ");
        String userInput = scanner.nextLine();

        // Trim leading and trailing spaces
        userInput = userInput.trim();

        // Check if the input is too long
        if (userInput.length() > 48) {
            System.out.println("O título do livro é muito longo.");
            return;
        }

        // Remove special characters (for example, replace "não" with "nao")
        userInput = removeSpecialCharacters(userInput);

        String url = apiUrl + userInput.replace(" ", "+");
        String response = consumoApi.obterDados(url);

        Conversor converter = new Conversor();
        try {
            BookApiResponse bookApiResponse = converter.obterDados(response, BookApiResponse.class);
            List<BookDto> books = bookApiResponse.getResults();

            // Check if the response JSON is empty or no books found
            if (books.isEmpty()) {
                System.out.println("Nenhum livro encontrado para a pesquisa: " + userInput);
                return;
            }

            // Get the first book from the list
            BookDto firstBook = books.get(0);

            // Print details of the first book
            System.out.println(firstBook);

            // Save the first book and its authors to the database
            saveToDatabase(firstBook);
        } catch (RuntimeException e) {
            System.out.println("Erro ao processar resposta JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String removeSpecialCharacters(String input) {
        // Replace special characters with their equivalents
        input = input.replaceAll("[áàâã]", "a");
        input = input.replaceAll("[éèê]", "e");
        input = input.replaceAll("[íìî]", "i");
        input = input.replaceAll("[óòôõ]", "o");
        input = input.replaceAll("[úùû]", "u");
        input = input.replaceAll("[ç]", "c");

        // Remove any remaining special characters
        input = input.replaceAll("[^\\p{ASCII}]", "");

        return input;
    }



    private void saveToDatabase(BookDto bookDto) {
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

        System.out.println("Livro salvo com sucesso!");
    }

}




