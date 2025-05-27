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
import com.bookman.lms.exception.ResourceNotFoundException;
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
    
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null); // Or throw a different exception if needed elsewhere
    }

    // ................... Create User .......................................................................    
    @Transactional 
    public User createUser(User user) {
    	
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' is already taken.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email '" + user.getEmail() + "' is already in use.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<String> defaultRoles = new HashSet<>();
            defaultRoles.add("ROLE_USER"); 
            user.setRoles(defaultRoles);
        }

        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        return userRepository.save(user);
    }

      
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    // ................... Delete User ................    
    @Transactional
    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }
    

}