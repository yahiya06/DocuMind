package com.documind.security;

import com.documind.user.User;
import com.documind.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String email = username.trim().toLowerCase(Locale.ROOT);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
				.password(user.getPasswordHash())
				.authorities("ROLE_" + user.getRole().name())
				.disabled(!user.isEnabled())
				.build();
	}
}
