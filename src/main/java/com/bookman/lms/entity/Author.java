package com.bookman.lms.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an Author entity in the Library Management System. Maps to the
 * 'author' table in the database with validation constraints.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "First name is required")
	@Column(name = "first_name", nullable = false, length = 255)
	private String firstname;

	@NotBlank(message = "Last name is required")
	@Column(name = "last_name", nullable = false, length = 255)
	private String lastName;

	@NotNull(message = "Birth date is required")
	@Past(message = "Birth date must be in the past")
	@Column(name = "birth_date", nullable = false)
	private LocalDate birthdate;

	@NotBlank(message = "Address is required")
	@Column(nullable = false, length = 255)
	private String address;

	@NotBlank(message = "Phone number is required")
	@Column(name = "phone_number", nullable = false, unique = true, length = 20)
	private String phoneNumber;

	@NotBlank(message = "Email is required")
	@Size(max = 50, message = "Email cannot exceed 50 characters")
	@Email(message = "Email should be valid")
	@Column(name = "email", nullable = false, unique = true, length = 50)
	private String email;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@Column(length = 255)
	private String remark;

	/**
	 * Calculates the author's current age based on birthdate
	 * 
	 * @return age in years, or null if birthdate is not set
	 */
	public Integer getAge() {
		if (this.birthdate == null) {
			return null;
		}
		return Period.between(this.birthdate, LocalDate.now()).getYears();
	}

	/**
	 * Gets the author's full name in standard format (First Last)
	 * 
	 * @return formatted full name
	 */
	public String getFullName() {
		return String.format("%s %s", this.firstname != null ? this.firstname : "",
				this.lastName != null ? this.lastName : "").trim();
	}

	/**
	 * Validates if all required contact information is present
	 * 
	 * @return true if has valid email and phone number
	 */
	public boolean hasCompleteContactInfo() {
		return this.email != null && !this.email.isEmpty() && this.phoneNumber != null && !this.phoneNumber.isEmpty();
	}

	/**
	 * Updates the author's address with validation
	 * 
	 * @param newAddress the new address to set
	 * @throws IllegalArgumentException if address is blank
	 */
	public void updateAddress(String newAddress) {
		if (newAddress == null || newAddress.trim().isEmpty()) {
			throw new IllegalArgumentException("Address cannot be blank");
		}
		this.address = newAddress.trim();
	}

	// =====================
	// BUILDER PATTERN SUPPORT
	// =====================
	/*
	 * Lombok @Builder provides: Author.builder() .firstname("John")
	 * .lastName("Doe") .birthdate(LocalDate.of(1950, 1, 1))
	 * .address("123 Author Lane") .phoneNumber("+1234567890")
	 * .email("johndoe@example.com") .remark("Pulitzer Prize winner") .build();
	 */
}