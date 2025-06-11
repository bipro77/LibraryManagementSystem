package com.bookman.lms.entity;

import java.math.BigDecimal;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book_loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookLoans {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * The book associated with this entity (e.g., loan or reservation).
	 * 
	 * This defines a many-to-one relationship, meaning multiple records
	 * (loans/reservations) can reference the same book. The relationship is lazily
	 * loaded, so the Book object will only be fetched from the database when
	 * explicitly accessed.
	 * 
	 * The foreign key column in the database is "book_id" and cannot be null.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	/**
	 * The user associated with this entity (e.g., the borrower or reserver).
	 * 
	 * This defines a many-to-one relationship, meaning many records can be linked
	 * to a single user. Like the book field, it is lazily loaded to improve
	 * performance.
	 * 
	 * The foreign key column in the database is "user_id" and is required (not
	 * null).
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private AppUser user;

	@NotNull(message = "Issue date must not be null")
	@Column(name = "issue_date", nullable = false)
	private LocalDate issueDate;

	@NotNull(message = "Due date must not be null")
	@Column(name = "due_date", nullable = false)
	private LocalDate dueDate;

	@Column(name = "return_date")
	private LocalDate returnDate;

	/**
	 * The fine amount associated with a loan or reservation. Must be zero or a
	 * positive value (no negative fines allowed). Stored in the database with a
	 * precision of 10 digits and 2 decimal places. Defaults to zero if not
	 * explicitly set, using Lombok's @Builder.Default.
	 */
	@PositiveOrZero
	@Column(name = "fine_amount", precision = 10, scale = 2)
	@Builder.Default
	private BigDecimal fineAmount = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private LoanStatus status = LoanStatus.PENDING;

	@Column(length = 500)
	private String remark;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	/**
	 * Automatic validation hook that executes before database persistence
	 * operations. Ensures data integrity by validating both date logic and status
	 * consistency.
	 * 
	 * This method is automatically invoked by JPA: - Before initial persistence
	 * (@PrePersist) - Before any update (@PreUpdate)
	 * 
	 * Combines multiple validation checks: 1. Validates chronological date
	 * relationships (issue/due/return dates) 2. Verifies status field consistency
	 * with other attributes
	 * 
	 * @see #validateDateLogic()
	 * @see #validateStatusConsistency()
	 */
	@PrePersist
	@PreUpdate
	private void validateDatesAndStatus() {
		validateDateLogic();
		validateStatusConsistency();
	}

	/**
	 * Validates the chronological logic between issue date, due date, and return
	 * date. Ensures all dates follow logical business rules for book loans.
	 *
	 * Rules validated: - Issue date cannot be in the future - Due date must be
	 * after issue date - Return date (if provided) cannot be before issue date -
	 * Return date cannot be more than 1 day in the future (allowing processing
	 * buffer)
	 *
	 * @throws InvalidDateException if any date validation fails, with specific
	 *                              message indicating which rule was violated
	 */
	private void validateDateLogic() {
		LocalDate today = LocalDate.now();

		if (issueDate.isAfter(today)) {
			throw new InvalidDateException("Issue date cannot be in the future");
		}

		if (!dueDate.isAfter(issueDate)) {
			throw new InvalidDateException("Due date must be after issue date");
		}

		if (returnDate != null) {
			if (returnDate.isBefore(issueDate)) {
				throw new InvalidDateException("Return date cannot be before issue date");
			}
			if (returnDate.isAfter(today.plusDays(1))) { // Allow 1 day buffer
				throw new InvalidDateException("Return date cannot be in the future");
			}
		}
	}

	/**
	 * Validates the logical consistency between the loan status and related fields.
	 * Ensures the status accurately reflects the real-world state of the loan.
	 * 
	 * Checks performed: - If status is RETURNED, verifies return date is set - If
	 * status is OVERDUE, verifies the loan is actually overdue
	 *
	 * @throws IllegalStateException when status contradicts other field values with
	 *                               specific messages indicating the exact
	 *                               inconsistency found
	 */
	private void validateStatusConsistency() {
		if (status == LoanStatus.RETURNED && returnDate == null) {
			throw new IllegalStateException("Return date must be set when status is RETURNED");
		}

		if (status == LoanStatus.OVERDUE && !isOverdue()) {
			throw new IllegalStateException("Status cannot be OVERDUE when loan is not actually overdue");
		}
	}

	/**
	 * Checks if the loan is currently overdue. A loan is considered overdue if: -
	 * The current date is past the due date - The book has not been returned
	 * 
	 * @return true if the loan is past due and not returned, false otherwise
	 */
	public boolean isOverdue() {
		return LocalDate.now().isAfter(dueDate) && !isReturned();
	}

	/**
	 * Determines if the book has been returned.
	 * 
	 * @return true if a return date is recorded, false otherwise
	 */
	public boolean isReturned() {
		return returnDate != null;
	}

	/**
	 * Checks if the loan is currently active.
	 * 
	 * @return true if the loan status is ACTIVE, false otherwise
	 */
	public boolean isActive() {
		return status == LoanStatus.ACTIVE;
	}

	/**
	 * Represents the status of a book loan in the library system.
	 */
	public enum LoanStatus {
		PENDING, // Loan requested but not approved
		APPROVED, // Loan approved but not issued
		ACTIVE, // Book is checked out
		OVERDUE, // Not returned by due date
		RETURNED, // Book returned
		CANCELLED, // Loan was cancelled
		REJECTED // Loan request was rejected
	}
}