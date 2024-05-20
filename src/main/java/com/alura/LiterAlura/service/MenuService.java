package com.alura.LiterAlura.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void buscaPorTituloDoLivro() {
        search("Enter book title: ", "title");
    }

    public void buscaPorAutor() {
        search("Enter author name: ", "author");
    }

    private void search(String prompt, String parameter) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        String userInput = scanner.nextLine();
        String url = apiUrl + userInput.replace(" ", "+");
        String response = consumoApi.obterDados(url);
        System.out.println(response);
    }
}
