package com.bookman.lms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookman.lms.entity.Book;
import com.bookman.lms.entity.User;
import com.bookman.lms.exception.BookNotFoundException;
import com.bookman.lms.service.BookService;
import com.bookman.lms.service.CustomUserDetailsService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private CustomUserDetailsService userService;

	public UserController(CustomUserDetailsService userService) {
		this.userService = userService;

	}

	/**
	 * GET /api/books Retrieves all books.
	 * 
	 * @return ResponseEntity with a list of books and HTTP status 200 OK.
	 */
	@GetMapping("/{id}")
	public UserDetails getUser(@PathVariable Long id) {
		UserDetails user = userService.loadUserByUsername("abc");
		System.out.println(user);
		return user;
	}
//	public ResponseEntity<Book> getAllUsers(@PathVariable Long id) {
//		UserDetails user = userService.loadUserByUsername("abc");
//		System.out.println("Hello world");
//		return bookService.getBookById(id).map(book -> new ResponseEntity<>(book, HttpStatus.OK))
//				.orElseThrow(() -> new BookNotFoundException("Book with ID  not found."));
//	}

	/**
	 * POST /api/books Creates a new book.
	 * 
	 * @param book The Book object to be created (sent in the request body).
	 * @return ResponseEntity with the created book and HTTP status 201 CREATED.
	 */
	@PostMapping
	public User createUser(@RequestBody User user) {
		User createdUser = userService.createUser(user);
		
		return createdUser;
	}

}
