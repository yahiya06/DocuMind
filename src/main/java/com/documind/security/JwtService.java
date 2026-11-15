package com.documind.security;

import com.documind.user.Role;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {

	private final JwtEncoder jwtEncoder;
	private final JwtProperties jwtProperties;
	private final Clock clock;

	public JwtService(JwtEncoder jwtEncoder, JwtProperties jwtProperties, Clock clock) {
		this.jwtEncoder = jwtEncoder;
		this.jwtProperties = jwtProperties;
		this.clock = clock;
	}

	public AccessToken createAccessToken(UUID userId, Role role) {
		return createAccessToken(userId, role, null);
	}

	public AccessToken createAccessToken(UUID userId, Role role, String email) {
		Instant issuedAt = clock.instant();
		Instant expiresAt = issuedAt.plus(jwtProperties.accessTokenTtl());
		JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
				.subject(userId.toString())
				.claim("roles", List.of(role.name()))
				.issuer(jwtProperties.issuer())
				.issuedAt(issuedAt)
				.expiresAt(expiresAt)
				.id(UUID.randomUUID().toString());
		if (email != null) {
			claimsBuilder.claim("email", email);
		}
		JwtClaimsSet claims = claimsBuilder.build();

		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		String value = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
		return new AccessToken(value, jwtProperties.accessTokenTtl().toSeconds());
	}
}
