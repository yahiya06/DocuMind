package com.documind.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
		name = "users",
		uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email")
)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "email", nullable = false, length = 254)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 100)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 20)
	private Role role;

	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected User() {
	}

	public User(String email, String passwordHash, Role role) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = role;
		this.enabled = true;
		this.createdAt = Instant.now();
	}

	public UUID getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public Role getRole() {
		return role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
