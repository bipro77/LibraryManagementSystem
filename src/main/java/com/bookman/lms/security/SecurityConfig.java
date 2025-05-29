package com.bookman.lms.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	DataSource dataSource;

	@Autowired
	UserDetailsService userDetailsService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/public", "/api/auth/**", "/error", "/api/users/register", "/api/users/{username}")
				.permitAll().requestMatchers("/admin/**", "/api/users/**").hasRole("ADMIN")
				.requestMatchers("/api/user/**", "/api/books/**").hasAnyRole("USER", "ADMIN").anyRequest()
				.authenticated()).httpBasic(Customizer.withDefaults()) // ✅ Modern way to enable Basic Auth for Postman

				.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true) // Redirect to home on
																							// successful login
						.failureUrl("/login?error") // Redirect to login page with error on failure
						.permitAll())

				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout")
						.invalidateHttpSession(true).deleteCookies("JSESSIONID")
						// .addLogoutHandler(customLogoutHandler) // Add your custom handler
						.permitAll())

				.csrf(csrf -> csrf.disable()); // Temporarily disable CSRF for easier testing (NOT recommended for
												// production)
												// For production, ensure CSRF tokens are handled in forms.
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}