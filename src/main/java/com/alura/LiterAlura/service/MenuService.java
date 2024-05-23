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

import java.util.*;

@Service
public class MenuService {

    private String apiUrl;

    private final String SEARCH_API_URL = "http://gutendex.com/books?search=";
    private final String POPULAR_API_URL = "http://gutendex.com/books?popular";

    @Autowired
    private ConsumoApi consumoApi;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    Scanner scanner = new Scanner(System.in);

    public void buscaPorTituloDoLivro() {

        String apiUrl = SEARCH_API_URL;

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
        livro = livroRepository.save(livro);

        // Iterate through authors
        for (AuthorDto authorDto : bookDto.authors()) {
            // Check if author already exists
            Optional<Autor> existingAutor = autorRepository.findByName(authorDto.name());

            Autor autor;
            if (existingAutor.isPresent()) {
                autor = existingAutor.get();
            } else {
                autor = new Autor();
                autor.setName(authorDto.name());
                autor.setBirthYear(authorDto.birth_year());
                autor.setDeathYear(authorDto.death_year());
                autor = autorRepository.save(autor);
            }

            // Add livro to the autor's livros list
            if (!autor.getLivros().contains(livro)) {
                autor.getLivros().add(livro);
                autorRepository.save(autor);
            }

            if (!livro.getAutores().contains(autor)) {
                livro.getAutores().add(autor);
                livroRepository.save(livro);
            }
        }

        //System.out.println("Livro salvo com sucesso!");
    }

    public void listarLivros() {
        try {
            List<Livro> livros = livroRepository.findAll();
            if (livros.isEmpty()) {
                System.out.println("Nenhum livro encontrado para a pesquisa.");
                return;
            }

            System.out.println("Lista de livros encontrados (" + livros.stream().count() +"):");
            for (Livro livro : livros) {
                System.out.println(livro.toString());
            }

        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao listar os livros: " + e.getMessage());
        }
    }

    public void listarAutores() {
        try {
            List<Autor> autores = autorRepository.findAll();
            if (autores.isEmpty()) {
                System.out.println("Nenhum autor encontrado para a pesquisa.");
                return;
            }
            Collections.sort(autores, Comparator.comparing(Autor::getName));

            System.out.println("Lista de autores encontrados (" + autores.stream().count() + "):");
            for (Autor autor : autores) {
                System.out.println(autor.toString());
            }

        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao listar os autores: " + e.getMessage());
        }
    }

    public void listarAutoresVivosPorData() {
        try {
            System.out.print("Digite o ano inicial: ");
            int dataInicial = scanner.nextInt();
            System.out.print("Digite o ano final: ");
            int dataFinal = scanner.nextInt();

            if (dataInicial > dataFinal) {
                System.out.println("Erro: O ano inicial precisa ser anterior ao ano final.");
                return;
            }

            List<Autor> autoresVivos = autorRepository.findByDate(dataInicial, dataFinal);
            if (autoresVivos.isEmpty()) {
                System.out.println("Nenhum autor vivo encontrado para o período especificado");
                return;
            }

            Collections.sort(autoresVivos, Comparator.comparing(Autor::getName));

            System.out.println("Estes autores estavam vivos entre " + dataInicial +
                    " e " + dataFinal + " (" + autoresVivos.stream().count() + "):");
            for (Autor autor : autoresVivos) {
                System.out.println(autor.toString());
            }
        } catch (InputMismatchException e) {
            System.out.println("Erro: Entrada inválida. Por favor, insira valores numéricos para os anos.");
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao listar os autores vivos: " + e.getMessage());
        }
    }

    public void listarLivrosPorIdioma() {
        // Define the valid language options
        Map<String, String> languageNames = new HashMap<>();
        languageNames.put("pt", "português");
        languageNames.put("en", "inglês");
        languageNames.put("es", "espanhol");
        languageNames.put("fr", "francês");

        Set<String> validLanguages = new HashSet<>(languageNames.keySet());

        System.out.println("""
                pt - Português 
                en - Inglês 
                es - Espanhol
                fr - Francês
                """);
        System.out.print("Selecione o idioma que deseja buscar (sigla): ");

        String idioma = scanner.nextLine().toLowerCase();

        if (!validLanguages.contains(idioma)) {
            System.out.println("Por favor, digite um idioma válido.");
            return;
        }

        try {
            List<Livro> livrosFiltrados = livroRepository.findByLanguagesContaining(idioma);
            if (livrosFiltrados.isEmpty()) {
                System.out.println("Nenhum livro encontrado para o idioma selecionado.");
                return;
            }

            // Calculate statistics using streams
            long numAutores = livrosFiltrados.stream()
                    .flatMap(livro -> livro.getAutores().stream())
                    .distinct()
                    .count();

            long numLivros = livrosFiltrados.size();

            System.out.println("Número de autores para o idioma " + languageNames.get(idioma) + ": " + numAutores);
            System.out.println("Número de livros para o idioma " + languageNames.get(idioma) + ": " + numLivros);

            System.out.println("Livros encontrados para o idioma " + languageNames.get(idioma) + ":");
            for (Livro livro : livrosFiltrados) {
                System.out.println(livro);
            }
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao listar os livros: " + e.getMessage());
        }
    }

    public void top10downloads() {
        String apiUrl = POPULAR_API_URL;

        try {
            String response = consumoApi.obterDados(apiUrl);
            Conversor converter = new Conversor();
            try {
                BookApiResponse bookApiResponse = converter.obterDados(response, BookApiResponse.class);
                List<BookDto> books = bookApiResponse.getResults();

                // Check if the response JSON is empty or no books found
                if (books.isEmpty()) {
                    System.out.println("Nenhum livro encontrado para a pesquisa.");
                    return;
                }

                // Print details of the first 10 books
                System.out.println("Detalhes dos 10 primeiros livros:");
                for (int i = 0; i < 10 && i < books.size(); i++) {
                    BookDto book = books.get(i);
                    System.out.println("Livro " + (i + 1) + ":\n" + books.get(i).toString());
                    saveToDatabase(book);
                }

            } catch (Exception e) {
                System.out.println("Ocorreu um erro ao obter ou salvar os livros: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao obter os dados da API: " + e.getMessage());
        }
    }


}



