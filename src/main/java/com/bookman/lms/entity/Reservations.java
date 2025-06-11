package com.bookman.lms.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bookman.lms.exception.InvalidDateException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations")
public class Reservations {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * The user who made the reservation or loan.
	 * 
	 * This establishes a many-to-one relationship between this entity and the
	 * AppUser entity. Each reservation or loan is associated with one user, but a
	 * user can have many reservations or loans.
	 * 
	 * FetchType.LAZY is used to load the user data only when accessed.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private AppUser user;

	/**
	 * The book involved in the reservation or loan.
	 * 
	 * This establishes a many-to-one relationship between this entity and the Book
	 * entity. Each reservation or loan is linked to one book, but a book can be
	 * associated with multiple reservations or loans.
	 * 
	 * FetchType.LAZY is used to defer loading book details until explicitly
	 * requested.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Column(name = "reservation_date", nullable = false)
	@Builder.Default
	private LocalDate reservationDate = LocalDate.now();

	@Column(name = "expiry_date", nullable = false)
	private LocalDate expiryDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private ReservationStatus status = ReservationStatus.PENDING;

	@Column(length = 500)
	private String notes;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	/**
	 * Validates reservation date constraints before saving or updating the entity.
	 * 
	 * This method is triggered automatically by JPA lifecycle events: - Before the
	 * entity is persisted (inserted) - Before the entity is updated
	 * 
	 * Validations performed: 1. Ensures the expiry date is set (not null). 2.
	 * Checks that the expiry date is not earlier than the reservation date. 3.
	 * Prevents marking the reservation as COMPLETED before the expiry date has
	 * passed.
	 * 
	 * Throws: InvalidDateException if any validation rule is violated.
	 */
	@PrePersist
	@PreUpdate
	private void validateReservation() {
		if (expiryDate == null) {
			throw new InvalidDateException("Expiry date must be set");
		}

		if (expiryDate.isBefore(reservationDate)) {
			throw new InvalidDateException("Expiry date cannot be before reservation date");
		}

		if (status == ReservationStatus.COMPLETED && !isExpired()) {
			throw new InvalidDateException("Cannot mark as completed before expiry date");
		}
	}

	/**
	 * Checks whether the reservation is currently active and has not expired.
	 *
	 * @return true if the reservation status is ACTIVE and the expiry date is not
	 *         passed
	 */
	public boolean isActive() {
		return status == ReservationStatus.ACTIVE && !isExpired();
	}

	/**
	 * Determines if the reservation has expired based on the current date.
	 *
	 * @return true if today's date is after the expiry date
	 */
	public boolean isExpired() {
		return LocalDate.now().isAfter(expiryDate);
	}

	/**
	 * Determines if the reservation can be fulfilled (i.e., turned into a loan).
	 * 
	 * A reservation is eligible for fulfillment if it is active and not expired.
	 *
	 * @return true if the reservation is ACTIVE and still valid
	 */
	public boolean canBeFulfilled() {
		return status == ReservationStatus.ACTIVE && !isExpired();
	}

	/**
	 * Represents the various stages of a book reservation in the system.
	 */
	public enum ReservationStatus {
		PENDING, // Reservation request received
		APPROVED, // Reservation approved by staff
		ACTIVE, // Book is reserved for user
		CANCELLED, // Reservation was cancelled
		COMPLETED, // Reservation was fulfilled (book loaned)
		EXPIRED, // Reservation expired without fulfillment
		REJECTED // Reservation request was rejected
	}

	/**
	 * Builder class for customizing Reservation entity construction. Allows setting
	 * optional fields with default values during object creation.
	 */
	public static class ReservationsBuilder {
		private LocalDate expiryDate = LocalDate.now().plusDays(7); // Default 7 day expiry
	}
}