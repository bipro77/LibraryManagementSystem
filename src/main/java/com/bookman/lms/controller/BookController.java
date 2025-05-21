package com.bookman.lms.controller;

import com.bookman.lms.entity.Book;
import com.bookman.lms.service.BookService;
import com.bookman.lms.exception.BookNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Book resources.
 * Handles HTTP requests and delegates to the BookService for business logic.
 */
@RestController // Marks this class as a REST controller
@RequestMapping("/api/books") // Base URL for all endpoints in this controller
public class BookController {

    private final BookService bookService;

    @Autowired // Injects BookService instance
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * GET /api/books
     * Retrieves all books.
     * @return ResponseEntity with a list of books and HTTP status 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    /**
     * GET /api/books/{id}
     * Retrieves a book by its ID.
     * @param id The ID of the book.
     * @return ResponseEntity with the book and HTTP status 200 OK if found,
     * or HTTP status 404 NOT_FOUND if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(book -> new ResponseEntity<>(book, HttpStatus.OK))
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found."));
    }

    /**
     * POST /api/books
     * Creates a new book.
     * @param book The Book object to be created (sent in the request body).
     * @return ResponseEntity with the created book and HTTP status 201 CREATED.
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = bookService.createBook(book);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    /**
     * PUT /api/books/{id}
     * Updates an existing book.
     * @param id The ID of the book to update.
     * @param book The Book object with updated information (sent in the request body).
     * @return ResponseEntity with the updated book and HTTP status 200 OK.
     * @throws BookNotFoundException if the book with the given ID is not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        Book updatedBook = bookService.updateBook(id, book);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    /**
     * DELETE /api/books/{id}
     * Deletes a book by its ID.
     * @param id The ID of the book to delete.
     * @return ResponseEntity with HTTP status 204 NO_CONTENT if successful.
     * @throws BookNotFoundException if the book with the given ID is not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /**
     * POST /api/books/batch
     * Creates multiple books at the same time
     * 
     * @param books A list of Book objects to be created (sent in the request body as a JSON array).
     * @return ResponseEntity with the list of created books and HTTP status 201 CREATED.
     */
    @PostMapping("/batch")
    public ResponseEntity<List<Book>> createMultipleBooks(@RequestBody List<Book> books) {
    	List<Book> createdBooks = bookService.createMultipleBooks(books);
    	return new ResponseEntity<>(createdBooks, HttpStatus.CREATED);
    }
}