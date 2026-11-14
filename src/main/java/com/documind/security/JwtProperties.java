package com.documind.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.Base64;

@Validated
@ConfigurationProperties(prefix = "documind.jwt")
public record JwtProperties(
		@NotBlank String secret,
		@NotBlank String issuer,
		@NotNull Duration accessTokenTtl
) {

	public JwtProperties {
		if (secret != null) {
			byte[] decodedSecret;
			try {
				decodedSecret = Base64.getDecoder().decode(secret);
			} catch (IllegalArgumentException exception) {
				throw new IllegalStateException("JWT secret must be valid Base64", exception);
			}
			if (decodedSecret.length < 32) {
				throw new IllegalStateException("JWT secret must decode to at least 32 bytes");
			}
		}
		if (accessTokenTtl != null && (accessTokenTtl.isZero() || accessTokenTtl.isNegative())) {
			throw new IllegalStateException("JWT access token TTL must be positive");
		}
	}

	@Override
	public String toString() {
		return "JwtProperties[secret=[REDACTED], issuer=" + issuer + ", accessTokenTtl=" + accessTokenTtl + "]";
	}
}
