package com.bookman.lms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bookman.lms.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	@Autowired
	JwtAuthFilter authFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/api/books/**", "/api/auth/**", "api/session/auth/**", "/error", "/api/users/{username}").permitAll()
				.requestMatchers("/admin/**", "/api/users/**").hasRole("ADMIN").requestMatchers("/api/books/**")
				.hasAnyRole("USER", "ADMIN").anyRequest().authenticated());

//		http.httpBasic(Customizer.withDefaults()); // using jwt so no need for this 
//		http.formLogin(Customizer.withDefaults()); // Using Postman so httpBasic is in use

		// If we want to create STATELESS session then we have to use it
//		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
		http.csrf(csrf -> csrf.disable());
		http.cors(cors -> cors.disable());
		return http.build();
	}

	/**
	 * This is optional, newer spring boot already using DaoAuthenticationProvider
	 * if it gets UserDetailsService and PasswordEncoder but for clear view we are
	 * adding it
	 * 
	 * @return provider DaoAuthenticationProvider
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(customUserDetailsService);
		return provider;
	}

	/**
	 * PasswordEncoder for the spring security
	 * 
	 * @return BCryptPasswordEncoder object
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// NOT EXECUTING
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
