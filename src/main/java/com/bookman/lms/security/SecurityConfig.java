package com.bookman.lms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/public", "/login", "/error", "/api/books/**", "api/users/**").permitAll() // Allow public access to these paths
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true) // Redirect to home on successful login
                .failureUrl("/login?error") // Redirect to login page with error on failure
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // Default logout URL
                .logoutSuccessUrl("/login?logout") // Redirect to login page after logout
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // Temporarily disable CSRF for easier testing (NOT recommended for production)
                                           // For production, ensure CSRF tokens are handled in forms.
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}