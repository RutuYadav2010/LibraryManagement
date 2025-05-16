package com.lighthouse.library.controller;
import com.lighthouse.library.model.Book;
import com.lighthouse.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/books")
@CrossOrigin(origins="http://localhost:8000")

public class BookController {
    @Autowired
    private BookRepository bookRepository;

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addBook(@RequestBody Book book) {
        book.setIssued(false);
        bookRepository.save(book);
        return ResponseEntity.ok(Map.of("message", "Book added successfully"));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookRepository.findAll());
    }

    @DeleteMapping("/delete-book")
    public ResponseEntity <Map<String,String>> deleteBook(@RequestBody Map<String,String> body) {
        Long id;
        try {
            id = Long.parseLong(body.get("id"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid Book ID"));
        }

        Optional<Book> book = bookRepository.findById(id);
        if(book.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No book found with ID" + id));
        }

        bookRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
    }

    @PutMapping("/update-book/{id}")
    public ResponseEntity <Map<String, String>> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if(optionalBook.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No book found with ID" + id));
        }

        Book existingBook = optionalBook.get();
        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setAuthor(bookDetails.getAuthor());
        existingBook.setGenre(bookDetails.getGenre());
        existingBook.setIssued(bookDetails.isIssued());
        existingBook.setIssued(bookDetails.isIssued());
        existingBook.setIssuedTo(bookDetails.getIssuedTo());
        existingBook.setIssueDate(bookDetails.getIssueDate());
        existingBook.setDueDate(bookDetails.getDueDate());

        bookRepository.save(existingBook);
        return ResponseEntity.ok(Map.of("message", "Book updated successfully"));

    }

    @PutMapping("/issue/{id}")
    public ResponseEntity<Map<String, String>> issueBook(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Optional<Book> optionalBook = bookRepository.findById(id);

        if(optionalBook.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No book found associated with ID" + id));
        }

        Book book = optionalBook.get();
        book.setIssued(true);
        book.setIssuedTo(body.get("issuedTo"));
        book.setIssueDate(java.time.LocalDate.now());
        book.setDueDate(java.time.LocalDate.now().plusDays(14));

        bookRepository.save(book);
        return ResponseEntity.ok(Map.of("message", "Book isssued successfully"));
    }

    @PutMapping("/return/{id}")
    public ResponseEntity<Map<String, String>> returnBook(@PathVariable Long id, @RequestBody Map<String, String> body){
        Optional<Book> optionalBook = bookRepository.findById(id);
        if(optionalBook.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No Book associated with ID" + id));
        }

        Book book = optionalBook.get();
        book.setIssued(false);
        book.setIssuedTo(null);
        book.setIssueDate(null);
        book.setDueDate(null);

        bookRepository.save(book);
        return ResponseEntity.ok(Map.of("message", "Book has been returned sucessfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
           @RequestParam(required = false) String title,
           @RequestParam(required = false) String author,
           @RequestParam(required = false) String genre) {

        List<Book> allBooks = bookRepository.findAll();
        List<Book> filteredBooks = allBooks.stream().filter(book -> title == null || book.getTitle().toLowerCase().contains(title.toLowerCase())).filter(book -> author == null || book.getAuthor().toLowerCase().contains(author.toLowerCase())).filter(book -> genre == null || book.getGenre().toLowerCase().contains(genre.toLowerCase())).toList();
        return ResponseEntity.ok(filteredBooks);

    }


}
