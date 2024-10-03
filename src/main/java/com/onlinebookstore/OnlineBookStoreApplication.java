package com.onlinebookstore;

import com.onlinebookstore.model.Book;
import com.onlinebookstore.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineBookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("Peace");
            book.setAuthor("Stiv Clow");
            book.setIsbn("26557686");
            book.setPrice(BigDecimal.valueOf(456));
            book.setDescription("Fine book!");
            bookService.save(book);
            System.out.println(bookService.findAll());
        };

    }
}
