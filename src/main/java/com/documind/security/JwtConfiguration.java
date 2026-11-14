package com.documind.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;

@Configuration
public class JwtConfiguration {

	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

	@Bean
	public SecretKey jwtSecretKey(JwtProperties jwtProperties) {
		byte[] secret = Base64.getDecoder().decode(jwtProperties.secret());
		return new SecretKeySpec(secret, "HmacSHA256");
	}

	@Bean
	public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
		return new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(jwtSecretKey));
	}

	@Bean
	public JwtDecoder jwtDecoder(SecretKey jwtSecretKey, JwtProperties jwtProperties, Clock clock) {
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey)
				.macAlgorithm(MacAlgorithm.HS256)
				.build();

		JwtTimestampValidator timestampValidator = new JwtTimestampValidator(Duration.ofSeconds(5));
		timestampValidator.setClock(clock);
		OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
				new JwtIssuerValidator(jwtProperties.issuer()),
				timestampValidator
		);
		decoder.setJwtValidator(validator);
		return decoder;
	}
}
