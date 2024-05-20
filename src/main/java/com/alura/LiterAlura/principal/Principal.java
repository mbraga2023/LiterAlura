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
            System.out.println("Menu:");
            System.out.println("1. Search book");
            System.out.println("2. Search author");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    menuService.buscaPorTituloDoLivro();
                    break;
                case 2:
                    menuService.buscaPorAutor();
                    break;
                case 3:
                    menu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter again.");
            }
        }
    }
}
