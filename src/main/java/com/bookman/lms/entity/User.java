package com.bookman.lms.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")

public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@NotBlank
	@Size(max = 20)
	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@NotBlank
	@Size(max = 50)
	@Email
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Size(max = 120)
	@Column(name = "password", nullable = false)
	private String password;

	private boolean accountNonLocked = true;
	private boolean accountNonExpired = true;
	private boolean credentialsNonExpired = true;
	private boolean enabled = true;

	private LocalDate credentialsExpiryDate;
	private LocalDate accountExpiryDate;

	private String twoFactorSecret;
	private boolean isTwoFactorEnabled = false;
	private String signUpMethod;

	@ElementCollection(fetch = FetchType.EAGER) // Roles will be eagerly loaded
	@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "role")
	private Set<String> roles; // e.g., "ROLE_USER", "ROLE_ADMIN"

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public User(String username, String email, String password, Set<String> roles) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}

	public User(String username, String email) {
		this.username = username;
		this.email = email;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(accountExpiryDate, other.accountExpiryDate)
				&& accountNonExpired == other.accountNonExpired && accountNonLocked == other.accountNonLocked
				&& Objects.equals(createdAt, other.createdAt)
				&& Objects.equals(credentialsExpiryDate, other.credentialsExpiryDate)
				&& credentialsNonExpired == other.credentialsNonExpired && Objects.equals(email, other.email)
				&& enabled == other.enabled && isTwoFactorEnabled == other.isTwoFactorEnabled
				&& Objects.equals(password, other.password) && Objects.equals(roles, other.roles)
				&& Objects.equals(signUpMethod, other.signUpMethod)
				&& Objects.equals(twoFactorSecret, other.twoFactorSecret) && Objects.equals(updatedAt, other.updatedAt)
				&& Objects.equals(userId, other.userId) && Objects.equals(username, other.username);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountExpiryDate, accountNonExpired, accountNonLocked, createdAt, credentialsExpiryDate,
				credentialsNonExpired, email, enabled, isTwoFactorEnabled, password, roles, signUpMethod,
				twoFactorSecret, updatedAt, userId, username);
	}

	/*
	 * Set createdAt and updatedAt field to current time while creating new data
	 */
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	/*
	 * Set updatedAt field to current time while updating the data
	 */
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
