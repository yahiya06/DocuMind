package com.documind.auth;

import com.documind.security.AccessToken;
import com.documind.security.JwtService;
import com.documind.user.Role;
import com.documind.user.User;
import com.documind.user.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtService jwtService
	) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	public UserResponse register(RegisterRequest request) {
		String email = normalizeEmail(request.email());
		User user = new User(email, passwordEncoder.encode(request.password()), Role.USER);

		try {
			userRepository.saveAndFlush(user);
		} catch (DataIntegrityViolationException exception) {
			throw new EmailAlreadyExistsException();
		}

		return toUserResponse(user);
	}

	public TokenResponse login(LoginRequest request) {
		String email = normalizeEmail(request.email());
		try {
			authenticationManager.authenticate(
					UsernamePasswordAuthenticationToken.unauthenticated(email, request.password())
			);
		} catch (AuthenticationException exception) {
			throw new InvalidCredentialsException();
		}

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Authenticated user was not found"));
		AccessToken accessToken = jwtService.createAccessToken(user.getId(), user.getRole(), user.getEmail());
		return new TokenResponse(accessToken.value(), "Bearer", accessToken.expiresInSeconds());
	}

	private static String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}

	private static UserResponse toUserResponse(User user) {
		return new UserResponse(user.getId(), user.getEmail(), user.getRole().name());
	}
}
