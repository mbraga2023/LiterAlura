package com.alura.LiterAlura.principal;

import com.alura.LiterAlura.service.ConsumoApi;
import com.alura.LiterAlura.service.MenuService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class Principal {

    @Autowired
    private ConsumoApi consumoApi;

    @Autowired
    private MenuService menuService; // Import MenuService



    public void exibeMenu() {
        boolean menu = true;
        Scanner scanner = new Scanner(System.in);

        while (menu) {

            System.out.println("""
                    **** LiterAlura ****
                    
                    Menu
                    1 - Buscar livro por título
                    2 - Listar livros registrados
                    3 - Listar autores
                    4 - Listar autores vivos por ano
                    5 - Listar livros por idioma
                                        
                    0 - Sair
                    """);

            System.out.print("Digite sua opção: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    menuService.buscaPorTituloDoLivro();
                    break;
                case 2:

                    break;
                case 0:
                    menu = false;
                    break;
                default:
                    System.out.println("Escolha inválida. Tente novamente");
            }
        }
    }
}
