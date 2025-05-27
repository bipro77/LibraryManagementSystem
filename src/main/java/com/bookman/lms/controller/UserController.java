package com.bookman.lms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookman.lms.entity.User;
import com.bookman.lms.exception.UserNotFoundException;
import com.bookman.lms.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;

	}

	/**
	 * GET /api/books Retrieves all users.
	 * 
	 * @return ResponseEntity with a list of users and HTTP status 200 OK.
	 */
	@GetMapping
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userService.getAllUsers();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	/**
	 * GET /api/users/{username} Retrieves a user by its Username.
	 * 
	 * @param username The Username of the user.
	 * @return ResponseEntity with the user and HTTP status 200 OK if found, or HTTP
	 *         status 404 NOT_FOUND if not found.
	 */
	@GetMapping("/{username}")
	public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
		return userService.getUserByUsername(username).map(user -> new ResponseEntity<>(user, HttpStatus.OK))
				.orElseThrow(() -> new UserNotFoundException("User with this username " + username + " not found."));
	}

	/**
	 * POST /api/users Creates a new user.
	 * 
	 * @param user The User object to be created (sent in the request body).
	 * @return ResponseEntity with the created user and HTTP status 201 CREATED.
	 */
	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user) {
		User createdUser = userService.createUser(user);
		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
	}

	/**
	 * PUT /api/users/{id} Updates an existing user.
	 * 
	 * @param id   The ID of the user to update.
	 * @param user The User object with updated information (sent in the request
	 *             body).
	 * @return ResponseEntity with the updated user and HTTP status 200 OK.
	 * @throws UserNotFoundException if the user with the given ID is not found.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
		User updatedUser = userService.updateUser(id, user);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	/**
	 * DELETE /api/users/{id} Deletes a User by its ID.
	 * 
	 * @param id The ID of the user to delete.
	 * @return ResponseEntity with HTTP status 204 NO_CONTENT if successful.
	 * @throws UserNotFoundException if the user with the given ID is not found.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
