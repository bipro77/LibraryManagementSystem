package com.bookman.lms.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookman.lms.entity.Book;
import com.bookman.lms.exception.ResourceNotFoundException;
import com.bookman.lms.repository.BookRepository;

@Service // Marks this class as a Spring service component
public class BookService {

	private final BookRepository bookRepository;

	// Injects BookRepository instance
	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	public Optional<Book> getBookById(Long id) {
		return bookRepository.findById(id);
	}

	@Transactional // Ensures the entire method executes as a single transaction
	public Book createBook(Book book) {
		return bookRepository.save(book);
	}

	@Transactional
	public Book updateBook(Long id, Book updatedBook) {
		return bookRepository.findById(id).map(existingBook -> {
			existingBook.setTitle(updatedBook.getTitle());
			existingBook.setIsbn(updatedBook.getIsbn());
			existingBook.setYear(updatedBook.getYear());
			existingBook.setGenre(updatedBook.getGenre());
			return bookRepository.save(existingBook);
		}).orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found."));
	}

	@Transactional
	public void deleteBook(Long id) {
		if (!bookRepository.existsById(id)) {
			throw new ResourceNotFoundException("Book with ID " + id + " not found.");
		}
		bookRepository.deleteById(id);
	}

	@Transactional
	public List<Book> createMultipleBooks(List<Book> books) {
		// saveAll method of JpaRepository efficiently saves a collection of entities
		return bookRepository.saveAll(books);
	}

}
