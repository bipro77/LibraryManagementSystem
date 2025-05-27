package com.bookman.lms.service;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bookman.lms.entity.User;
import com.bookman.lms.repository.UserRepository;

@Service // Marks this as a Spring service
public class CustomUserDetailsService implements UserDetailsService {

	// Inject PasswordEncoder for hashing
	private final UserRepository userRepository;

	// Constructor to inject dependencies
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Loads user-specific data. This method is called by Spring Security during the
	 * authentication process.
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
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				user.getRoles().stream().map(SimpleGrantedAuthority::new) // Convert role strings to GrantedAuthority
						.collect(Collectors.toList()));
	}

}