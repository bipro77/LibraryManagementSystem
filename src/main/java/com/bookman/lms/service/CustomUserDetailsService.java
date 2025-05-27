package com.bookman.lms.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; // Import PasswordEncoder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookman.lms.entity.Book;
import com.bookman.lms.entity.User;
import com.bookman.lms.repository.UserRepository;

@Service // Marks this as a Spring service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder for hashing

    // Constructor to inject dependencies
    public CustomUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Loads user-specific data. This method is called by Spring Security
     * during the authentication process.
     *
     * @param username The username to retrieve.
     * @return A UserDetails object containing the user's information.
     * @throws UsernameNotFoundException If the user cannot be found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Convert your User entity to Spring Security's UserDetails object
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.getRoles().stream()
                .map(SimpleGrantedAuthority::new) // Convert role strings to GrantedAuthority
                .collect(Collectors.toList())
        );
    }
    
    /**
     * Creates and saves a new user in the system.
     * This method handles checking for existing usernames/emails, encoding the password,
     * and assigning default roles if none are provided.
     *
     * @param user The User entity with plain-text password and other details for registration.
     * @return The saved User entity, typically with the generated ID.
     * @throws IllegalArgumentException if the username or email already exists.
     */
    @Transactional // Ensures the entire method executes as a single database transaction
    public User createUser(User user) {
        // Step 1: Validate uniqueness of username and email
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' is already taken.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email '" + user.getEmail() + "' is already in use.");
        }

        // Step 2: Encode the plain-text password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Step 3: Assign a default role if no roles are provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<String> defaultRoles = new HashSet<>();
            defaultRoles.add("ROLE_USER"); // Assign 'ROLE_USER' as the default role
            user.setRoles(defaultRoles);
        }

        // Step 4: Set default account status flags (if not already set in User constructor)
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        // Step 5: Save the new user to the database
        return userRepository.save(user);
    }

    // You might also want to add a method for finding a user by username for other services/controllers
    // without throwing a UsernameNotFoundException, perhaps for simpler checks or other purposes.
    public User findUserByUsernameForBusinessLogic(String username) {
        return userRepository.findByUsername(username)
                .orElse(null); // Or throw a different exception if needed elsewhere
    }

    // Helper methods for existence checks, useful for validation layers
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}