package com.bookman.lms.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookman.lms.entity.AppUser;
import com.bookman.lms.exception.ResourceNotFoundException;
import com.bookman.lms.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * 
	 * @param username GET /api/users/{username} Retrieves a user details by its
	 *                 username.
	 * @return ResponseEntity with user details and HTTP status 200 OK or HTTP
	 *         status 404 NOT_FOUND if not found.
	 */

	@GetMapping("/{username}")
	@PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
	public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("Auth name: " + auth.getName());
		System.out.println("Authorities: " + auth.getAuthorities());
		System.out.println("Path variable: " + username);
		// Construct a Map containing only the non-sensitive user data for the API
		// response.
		Map<String, Object> userData = new HashMap<>();

		userService.getUserByUsername(username).map(user -> {
			userData.put("userId", user.getUserId()); // Your User entity uses userId for the ID
			userData.put("username", user.getUsername());
			userData.put("email", user.getEmail());

			// Add user roles (assuming getRoles() returns Set<String> directly)
			userData.put("roles", user.getRoles());

			// Include account status flags (optional, but good for client to know)
			userData.put("accountNonLocked", user.isAccountNonLocked());
			userData.put("accountNonExpired", user.isAccountNonExpired());
			userData.put("credentialsNonExpired", user.isCredentialsNonExpired());
			userData.put("enabled", user.isEnabled());
			return userData;
		}).orElseThrow(() -> new ResourceNotFoundException("User with ID " + username + " not found."));

		// Crucial: Do NOT include sensitive fields like user.getPassword() or
		// user.getTwoFactorSecret()

		return ResponseEntity.ok(userData); // Return 200 OK with the user data map
	}

	/**
	 * 
	 * @param userData
	 * @return
	 */
	@PostMapping("/register") // Handles POST requests to /api/users/register
	// @PreAuthorize("permitAll()") // This annotation can be added here if your
	// SecurityConfig doesn't already allow it.
	// However, configuring `permitAll()` for this path in `SecurityConfig` is
	// sufficient.
	public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody Map<String, Object> userData) {
		String username = (String) userData.get("username");
		String email = (String) userData.get("email");
		String plainPassword = (String) userData.get("password");

		if (username == null || username.isBlank() || email == null || email.isBlank() || plainPassword == null
				|| plainPassword.isBlank()) {
			// Return a specific error response for missing required fields
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("timestamp", java.time.LocalDateTime.now());
			errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
			errorResponse.put("error", "Bad Request");
			errorResponse.put("message", "Username, email, and password are required.");
			errorResponse.put("path", "/api/users/register");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}

		AppUser newUser = new AppUser();
		newUser.setUsername(username);
		newUser.setEmail(email);
		newUser.setPassword(plainPassword);

		Set<String> roles = new HashSet<>();
		if (userData.containsKey("roles") && userData.get("roles") instanceof Iterable) {
			// Iterate over the collection of roles provided in the request body
			((Iterable<?>) userData.get("roles")).forEach(roleObj -> {
				if (roleObj instanceof String) {
					roles.add((String) roleObj);
				}
			});
		}
		newUser.setRoles(roles);

		try {
			AppUser registeredUser = userService.createUser(newUser);
			Map<String, Object> responseData = new HashMap<>();
			responseData.put("userId", registeredUser.getUserId());
			responseData.put("username", registeredUser.getUsername());
			responseData.put("email", registeredUser.getEmail());
			responseData.put("roles", registeredUser.getRoles());
			responseData.put("message", "User registered successfully!");
			responseData.put("status", HttpStatus.CREATED.value()); // Add status to body for clarity

			// Return 201 Created status indicating successful resource creation
			return new ResponseEntity<>(responseData, HttpStatus.CREATED);

		} catch (IllegalArgumentException e) {
			// Catch specific exceptions thrown by the service (e.g., duplicate
			// username/email)
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("timestamp", java.time.LocalDateTime.now());
			errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
			errorResponse.put("error", "Bad Request");
			errorResponse.put("message", e.getMessage()); // Use the exception message
			errorResponse.put("path", "/api/users/register");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * PUT /api/users/{userId} Updates an existing user.
	 * 
	 * @param userId  The ID of the book to update.
	 * @param AppUser The AppUser object with updated information (sent in the
	 *                request body).
	 * @return ResponseEntity with the updated user and HTTP status 200 OK.
	 * @throws ResourceNotFoundException if the book with the given ID is not found.
	 */
	@PutMapping("/{userId}")
	public ResponseEntity<AppUser> updateUser(@PathVariable Long userId, @RequestBody AppUser userData) {
		AppUser updatedUser = userService.updateUser(userId, userData);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	/**
	 * DELETE /api/users/{userId} Deletes a user by its ID.
	 * 
	 * @param id The ID of the user to delete.
	 * @return ResponseEntity with HTTP status 204 NO_CONTENT if successful.
	 * @throws ResourceNotFoundException if the user with the given ID is not found.
	 */
	@DeleteMapping("/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
		userService.deleteUser(userId);
//		return ResponseEntity.noContent().build(); // another way to return NO_CONTENT response
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

// Have to find
//	@GetMapping("/me")
//	@PreAuthorize("isAuthenticated()")
//	public ResponseEntity<Map<String, Object>> getCurrentUserProfile(Authentication authentication) {
//		String username = authentication.getName();
//		User user = userService.getUserByUsername(username);
//
//		Map<String, Object> userData = new HashMap<>();
//		userData.put("userId", user.getUserId());
//		userData.put("username", user.getUsername());
//		userData.put("email", user.getEmail());
//		userData.put("roles", user.getRoles());
//		userData.put("accountNonLocked", user.isAccountNonLocked());
//		userData.put("accountNonExpired", user.isAccountNonExpired());
//		userData.put("credentialsNonExpired", user.isCredentialsNonExpired());
//		userData.put("enabled", user.isEnabled());
//
//		return ResponseEntity.ok(userData);
//	}
}
