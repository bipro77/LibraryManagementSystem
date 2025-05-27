package com.bookman.lms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookman.lms.entity.User;


public interface UserRepository extends JpaRepository<User, Long>{
	/**
     * Finds a User entity by their username.
     * Spring Data JPA will automatically generate the implementation for this method.
     *
     * @param username The username to search for.
     * @return An Optional containing the User if found, or an empty Optional if not found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user with the given username already exists in the database.
     * Useful for preventing duplicate registrations.
     *
     * @param username The username to check.
     * @return True if a user with the username exists, false otherwise.
     */
    public Boolean existsByUsername(String username);

    /**
     * Checks if a user with the given email already exists in the database.
     * Useful for preventing duplicate registrations.
     *
     * @param email The email to check.
     * @return True if a user with the email exists, false otherwise.
     */
    public Boolean existsByEmail(String email);

    // You can add more custom query methods here as needed,
    // following Spring Data JPA's naming conventions or using @Query annotation.

    // Example: Find users by email (if needed beyond existsByEmail)
    // Optional<User> findByEmail(String email);
}
