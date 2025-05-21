package com.bookman.lms.service;

import com.bookman.lms.entity.Book;
import com.bookman.lms.repository.BookRepository;
import com.bookman.lms.exception.BookNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing Book entities. Contains business logic and
 * interacts with the BookRepository.
 */
@Service // Marks this class as a Spring service component
public class BookService {

	private final BookRepository bookRepository;

	@Autowired // Injects BookRepository instance
	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	/**
	 * Retrieves all books from the database.
	 * 
	 * @return A list of all books.
	 */
	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	/**
	 * Retrieves a book by its ID.
	 * 
	 * @param id The ID of the book to retrieve.
	 * @return An Optional containing the Book if found, empty otherwise.
	 */
	public Optional<Book> getBookById(Long id) {
		return bookRepository.findById(id);
	}

	/**
	 * Creates a new book.
	 * 
	 * @param book The Book object to be created.
	 * @return The saved Book object (with generated ID).
	 */
	@Transactional // Ensures the entire method executes as a single transaction
	public Book createBook(Book book) {
		return bookRepository.save(book);
	}

	/**
	 * Updates an existing book.
	 * 
	 * @param id          The ID of the book to update.
	 * @param updatedBook The Book object with updated information.
	 * @return The updated Book object.
	 * @throws BookNotFoundException if the book with the given ID is not found.
	 */
	@Transactional
	public Book updateBook(Long id, Book updatedBook) {
		return bookRepository.findById(id).map(existingBook -> {
			existingBook.setTitle(updatedBook.getTitle());
			existingBook.setIsbn(updatedBook.getIsbn());
			existingBook.setYear(updatedBook.getYear());
			existingBook.setGenre(updatedBook.getGenre());
			return bookRepository.save(existingBook);
		}).orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found."));
	}

	/**
	 * Deletes a book by its ID.
	 * 
	 * @param id The ID of the book to delete.
	 * @throws BookNotFoundException if the book with the given ID is not found.
	 */
	@Transactional
	public void deleteBook(Long id) {
		if (!bookRepository.existsById(id)) {
			throw new BookNotFoundException("Book with ID " + id + " not found.");
		}
		bookRepository.deleteById(id);
	}

	/**
	 * Creates multiple new books.
	 * 
	 * @param books A list of Book objects to be created.
	 * @return A list of the saved Book objects (with generated IDs).
	 */
	@Transactional
	public List<Book> createMultipleBooks(List<Book> books) {
		// saveAll method of JpaRepository efficiently saves a collection of entities
		return bookRepository.saveAll(books);
	}

}