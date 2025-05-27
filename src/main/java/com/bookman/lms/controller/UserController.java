package com.bookman.lms.controller;

import com.bookman.lms.entity.User; 
import com.bookman.lms.exception.ResourceNotFoundException;
import com.bookman.lms.service.CustomUserDetailsService;
import com.bookman.lms.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController 
@RequestMapping("/api/users") 
public class UserController {

	private final CustomUserDetailsService userDetailsService;
	private final UserService userService;

	public UserController(CustomUserDetailsService userDetailsService, UserService userService) {
		this.userDetailsService = userDetailsService;
		this.userService = userService;
	}


	@GetMapping("/{username}") 
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username, Authentication authentication) {
		
		Optional<User> user = userService.getUserByUsername(username);
		if (user == null) {
			throw new ResourceNotFoundException("User not found with username: " + username);
		}

		// Construct a Map containing only the non-sensitive user data for the API
		// response.
		Map<String, Object> userData = new HashMap<>();
		userData.put("userId", user.getUserId()); // Your User entity uses userId for the ID
		userData.put("username", user.getUsername());
		userData.put("email", user.getEmail());
		// If your User entity has firstName/lastName, you'd add them here:
		// userData.put("firstName", user.getFirstName());
		// userData.put("lastName", user.getLastName());

		// Add user roles (assuming getRoles() returns Set<String> directly)
		userData.put("roles", user.getRoles());

		// Include account status flags (optional, but good for client to know)
		userData.put("accountNonLocked", user.isAccountNonLocked());
		userData.put("accountNonExpired", user.isAccountNonExpired());
		userData.put("credentialsNonExpired", user.isCredentialsNonExpired());
		userData.put("enabled", user.isEnabled());

		// Crucial: Do NOT include sensitive fields like user.getPassword() or
		// user.getTwoFactorSecret()

		return ResponseEntity.ok(userData); // Return 200 OK with the user data map
	}

	
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

		User newUser = new User();
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
			User registeredUser = userDetailsService.createUser(newUser);
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
	
	// ............................. Delete User ..................... Not configured
	@DeleteMapping("remove/{userId}")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
		userDetailsService.deleteUserById(userId); 
        // Return 204 No Content status, indicating successful processing with no content to return
        System.out.println(" remove Id "+  userId);
        return ResponseEntity.noContent().build();
    }

	
	
	
// Have to find
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUserProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userDetailsService.findUserByUsername(username);

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", user.getUserId());
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("roles", user.getRoles());
        userData.put("accountNonLocked", user.isAccountNonLocked());
        userData.put("accountNonExpired", user.isAccountNonExpired());
        userData.put("credentialsNonExpired", user.isCredentialsNonExpired());
        userData.put("enabled", user.isEnabled());

        return ResponseEntity.ok(userData);
    }
}

	
