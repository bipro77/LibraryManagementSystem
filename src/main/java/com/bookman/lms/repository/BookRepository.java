package com.bookman.lms.repository;

import com.bookman.lms.entity.Book;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Book entity.
 * Provides standard CRUD operations and allows for defining custom queries.
 */
@Repository // Marks this interface as a Spring Data JPA repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // JpaRepository provides methods like save(), findById(), findAll(), deleteById(), etc.
    // You can add custom query methods here if needed, e.g.,
     Optional<Book> findByIsbn(String isbn);
    // List<Book> findByTitleContainingIgnoreCase(String title);
}