package com.bookman.lms.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookman.lms.entity.AppUser;
import com.bookman.lms.exception.ResourceNotFoundException;
import com.bookman.lms.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Retrieves all users from the database.
	 * 
	 * @return A list of all users.
	 */
	public List<AppUser> getAllUsers() {
		return userRepository.findAll();
	}

	/**
	 * Retrieves a user by its ID.
	 * 
	 * @param username The Username of the user to retrieve.
	 * @return An Optional containing the User if found, empty otherwise.
	 */
	public Optional<AppUser> getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	/**
	 * Creates a new user.
	 * 
	 * @param appUser The User object to be created.
	 * @return The saved User object (with generated ID).
	 */
	@Transactional // Ensures the entire method executes as a single transaction

	public AppUser createUser(AppUser appUser) {

		if (userRepository.existsByUsername(appUser.getUsername())) {
			throw new IllegalArgumentException("Username '" + appUser.getUsername() + "' is already taken.");
		}
		if (userRepository.existsByEmail(appUser.getEmail())) {
			throw new IllegalArgumentException("Email '" + appUser.getEmail() + "' is already in use.");
		}

		appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));

		if (appUser.getRoles() == null || appUser.getRoles().isEmpty()) {
			Set<String> defaultRoles = new HashSet<>();
			defaultRoles.add("ROLE_USER");
			appUser.setRoles(defaultRoles);
		}

		appUser.setAccountNonLocked(true);
		appUser.setAccountNonExpired(true);
		appUser.setCredentialsNonExpired(true);
		appUser.setEnabled(true);

		return userRepository.save(appUser);
	}

	/**
	 * Updates an existing user.
	 * 
	 * @param id          The ID of the user to update.
	 * @param updatedUser The User object with updated information.
	 * @return The updated User object.
	 * @throws UserNotFoundException if the user with the given ID is not found.
	 */
	@Transactional
	public AppUser updateUser(Long id, AppUser updatedUser) {
		if (userRepository.existsByUsername(updatedUser.getUsername())) {
			throw new IllegalArgumentException("Username '" + updatedUser.getUsername() + "' is already taken.");
		}
		if (userRepository.existsByEmail(updatedUser.getEmail())) {
			throw new IllegalArgumentException("Email '" + updatedUser.getEmail() + "' is already in use.");
		}
		
		return userRepository.findById(id).map(existingUser -> {
			existingUser.setUsername(updatedUser.getUsername());
			existingUser.setEmail(updatedUser.getEmail());
			existingUser.setPassword(passwordEncoder.encode(existingUser.getPassword()));
			return userRepository.save(existingUser);
		}).orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found."));
	}

	/**
	 * Deletes a user by its ID.
	 * 
	 * @param id The ID of the user to delete.
	 * @throws UserNotFoundException if the user with the given ID is not found.
	 */
	@Transactional
	public void deleteUser(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("User with ID " + id + " not found.");
		}
		userRepository.deleteById(id);
	}
	
	
	public Boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	public Boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

}
