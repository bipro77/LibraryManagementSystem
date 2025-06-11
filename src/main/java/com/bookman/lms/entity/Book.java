package com.bookman.lms.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Book entity in the Library Management System. Maps to the
 * 'books' table in the database with validation constraints.
 */
@Data
@Entity
@Table(name = "books")
@NoArgsConstructor
@AllArgsConstructor
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Title is required")
	@Size(max = 255, message = "Title cannot exceed 255 characters")
	@Column(nullable = false, length = 255)
	private String title;

	@NotBlank(message = "ISBN is required")
	@Size(max = 13, message = "ISBN cannot exceed 13 characters")
	@Column(nullable = false, unique = true, length = 13)
	private String isbn;

	@Min(value = 0, message = "Year cannot be negative")
	@Column
	private Integer year; // year of edition

	@NotBlank(message = "Published by is required")
	@Size(max = 255, message = "Publisher name cannot exceed 255 characters")
	@Column(name = "published_by", nullable = false, length = 255)
	private String publishedBy;

	@NotNull(message = "Published date is required")
	@Column(name = "published_date", nullable = false)
	private LocalDate publishedDate;

	@Size(max = 100, message = "Genre cannot exceed 100 characters")
	@Column(length = 100)
	private String genre;

	@Size(max = 100, message = "Edition cannot exceed 100 characters")
	@Column
	private String edition;

	@NotBlank(message = "Remark is required")
	@Size(max = 255, message = "Remark cannot exceed 255 characters")
	@Column(nullable = false, length = 255)
	private String remark;

	@NotNull(message = "Total copies are required")
	@Min(value = 0, message = "Total copies cannot be negative")
	@Column(nullable = false)
	private Integer totalCopy;

	@NotNull(message = "Available copies are required")
	@Min(value = 0, message = "Available copies cannot be negative")
	@Column(nullable = false)
	private Integer availableCopy;

	@NotNull(message = "Creation timestamp is required")
	@Column(nullable = false)
	private LocalDateTime createdAt;

	@NotNull(message = "Update timestamp is required")
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	/**
	 * Sets timestamps when entity is first persisted
	 */
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	/**
	 * Updates timestamp when entity is modified
	 */
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	/**
	 * Checks if book is available for loan
	 * 
	 * @return true if available copies exist
	 */
	public boolean isAvailable() {
		return availableCopy != null && availableCopy > 0;
	}

	/**
	 * Processes book loan transaction
	 * 
	 * @throws IllegalStateException if no copies available
	 */
	public void loanOutCopy() {
		if (!isAvailable()) {
			throw new IllegalStateException("No copies available to loan out.");
		}
		availableCopy--;
	}

	/**
	 * Processes book return transaction
	 * 
	 * @throws IllegalStateException if inventory would exceed total copies
	 */
	public void returnCopy() {
		if (availableCopy >= totalCopy) {
			throw new IllegalStateException("Available copies cannot exceed total copies.");
		}
		availableCopy++;
	}

	/**
	 * Checks publication year against given year
	 * 
	 * @param yearToCheck the comparison year
	 * @return true if published before given year
	 */
	public boolean wasPublishedBefore(int yearToCheck) {
		if (publishedDate == null)
			return false;
		return publishedDate.getYear() < yearToCheck;
	}
}